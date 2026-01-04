package com.example.demo.modules.game.service;

import com.example.demo.constant.GameConstant;
import com.example.demo.enums.NotificationType;
import com.example.demo.modules.dashboard.dto.NotificationCreateDTO;
import com.example.demo.modules.dashboard.entity.UserStats;
import com.example.demo.modules.dashboard.service.NotificationService;
import com.example.demo.modules.dashboard.service.StatsService;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.modules.game.dao.BattleLogEntry;
import com.example.demo.modules.game.dao.DamageResult;
import com.example.demo.modules.game.dto.battle.BattleChallengeDTO;
import com.example.demo.modules.game.dto.battle.BattleResultDTO;
import com.example.demo.modules.game.dto.character.BattleCharacterDTO;
import com.example.demo.modules.game.entity.Battle;
import com.example.demo.modules.game.entity.CharacterInstance;
import com.example.demo.modules.game.entity.CharacterTemplate;
import com.example.demo.modules.game.repo.BattleMapper;
import com.example.demo.modules.game.repo.CharacterInstanceMapper;
import com.example.demo.modules.game.repo.CharacterTemplateMapper;
import com.example.demo.modules.game.utility.ConvertBattleCharacterUtil;
import com.example.demo.service.IdGeneratorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; // You'll need the Jackson library
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BattleService {

    private static final Log logger = LogFactory.getLog(BattleService.class);

    private final Random random = new Random();

    @Autowired
    private final CharacterInstanceMapper characterInstanceMapper;

    // inject the service, instead of direct repository for caching capability
    @Autowired
    private final CharacterService characterService;

    @Autowired
    private final BattleMapper battleMapper;

    @Autowired
    private final ObjectMapper objectMapper; // For converting the log to/from JSON string

    @Autowired
    private final StatsService statsService;

    @Autowired
    private final IdGeneratorService idGeneratorService;

    @Autowired
    private final NotificationService notificationService;

    @Value("${aws.s3.base-url}")
    private String imageBaseUrl;

    /**
     * Creates and simulates a battle between two characters.
     * Sends a notification to the opponent upon battle completion.
     *
     * @param dto The {@link BattleChallengeDTO} containing challenger and opponent character IDs
     * @return The {@link Battle} entity representing the simulated battle
     * @throws JsonProcessingException if any error occurs during JSON processing
     */
    @Transactional
    public Battle createAndSimulateBattle(BattleChallengeDTO dto) throws JsonProcessingException {
        String challengerId = dto.getChallengerCharacterId();
        String defenderId = dto.getOpponentCharacterId();
        logger.info("Simulating battle between challenger: " + challengerId + " and defender: " + defenderId);
        Optional<CharacterInstance> challengerOptional = Optional.ofNullable(characterInstanceMapper.findByCharacterId(challengerId));
        if (challengerOptional.isEmpty()) {
            logger.warn("Challenger does not exist: " + challengerId);
            throw new ResourceNotFoundException("Challenger does not exist: " + challengerId);
        }
        CharacterInstance challengerInstance = challengerOptional.get();
        UserStats challengerStats = statsService.getUserStats(challengerInstance.getUserId());
        CharacterTemplate challengerTemplate = characterService.findByTemplateId(challengerInstance.getCharacterTemplateId());
        BattleCharacterDTO challenger = ConvertBattleCharacterUtil.toBattleCharacterDTO(imageBaseUrl, challengerInstance, challengerTemplate, challengerStats);

        Optional<CharacterInstance> defenderOptional = Optional.ofNullable(characterInstanceMapper.findByCharacterId(defenderId));
        if (defenderOptional.isEmpty()) {
            logger.warn("Defender does not exist: " + defenderId);
            throw new ResourceNotFoundException("Defender does not exist: " + defenderId);
        }
        CharacterInstance defenderInstance = defenderOptional.get();
        UserStats defenderStats = statsService.getUserStats(defenderInstance.getUserId());
        CharacterTemplate defenderTemplate = characterService.findByTemplateId(defenderInstance.getCharacterTemplateId());
        BattleCharacterDTO defender = ConvertBattleCharacterUtil.toBattleCharacterDTO(imageBaseUrl, defenderInstance, defenderTemplate, defenderStats);

        int challengerCurrentHp = challenger.getStats().getHp();
        int defenderCurrentHp = defender.getStats().getHp();
        List<BattleLogEntry> battleLog = new ArrayList<>();
        int turnCounter = 1;

        BattleCharacterDTO currentAttacker = challenger.getStats().getSpd() >= defender.getStats().getSpd() ? challenger : defender;
        BattleCharacterDTO currentDefender = currentAttacker.getCharacterId().equals(challenger.getCharacterId()) ? defender : challenger;

        while (challengerCurrentHp > 0 && defenderCurrentHp > 0) {
            DamageResult damageResult = calculateDamage(currentAttacker, currentDefender);
            int damageDealt = damageResult.getDamage();

            if (currentDefender.getCharacterId().equals(challenger.getCharacterId())) {
                challengerCurrentHp -= damageDealt;
            } else {
                defenderCurrentHp -= damageDealt;
            }

            BattleLogEntry entry = new BattleLogEntry();
            entry.setTurn(turnCounter++);
            entry.setAttackerId(currentAttacker.getCharacterId());
            entry.setDefenderId(currentDefender.getCharacterId());
            entry.setDamage(damageDealt);

            // Set message based on whether hit was critical, and pick a random message from appropriate list
            String message;
            String animationToPlay;

            if (damageResult.isCritical()) {
                String template = GameConstant.CRITICAL_ATTACK_MESSAGES.get(random.nextInt(GameConstant.CRITICAL_ATTACK_MESSAGES.size()));
                message = String.format(template, currentAttacker.getCharacterName(), damageDealt);
                animationToPlay = currentAttacker.getCharacterType() + "2"; // Results in "Fire2", "Steel2", etc. for critical hit animation
            } else {
                String template = GameConstant.NORMAL_ATTACK_MESSAGES.get(random.nextInt(GameConstant.NORMAL_ATTACK_MESSAGES.size()));
                message = String.format(template, currentAttacker.getCharacterName(), damageDealt);
                animationToPlay = currentAttacker.getCharacterType() + "1"; // Standard sword attack animation
            }
            entry.setMessage(message);
            entry.setAnimationType(animationToPlay);

            battleLog.add(entry);

            // Swap roles for the next turn
            BattleCharacterDTO temp = currentAttacker;
            currentAttacker = currentDefender;
            currentDefender = temp;
        }

        BattleCharacterDTO winner = challengerCurrentHp > 0 ? challenger : defender;
        String winnerId = winner.getCharacterId();

        Battle battle = new Battle();
        battle.setBattleId(idGeneratorService.generateBattleId());
        battle.setChallengerId(challengerId);
        battle.setDefenderId(defenderId);
        battle.setWinnerId(winnerId);

        // Convert the log List to a JSON string for storage
        String logAsString = objectMapper.writeValueAsString(battleLog);
        battle.setBattleLog(logAsString);
        battle.setCreatedAt(Instant.now());
        battleMapper.insert(battle);

        if (winnerId.equals(challengerId)) {
            statsService.addExperience(challengerInstance.getUserId(), GameConstant.BATTLE_WIN_EXP);
        } else {
            statsService.addExperience(defenderInstance.getUserId(), GameConstant.BATTLE_WIN_EXP);
        }

        NotificationCreateDTO notification = getNotificationCreateDTO(dto, winnerId);

        notificationService.createNotification(notification);

        logger.info("Battle simulated: " + battle);

        return battle;
    }


    /**
     * Retrieves the battle result for a given battle ID.
     *
     * @param battleId The ID of the battle
     * @return The {@link BattleResultDTO} containing the battle result details
     * @throws JsonProcessingException if any error occurs during JSON processing
     */
    public BattleResultDTO getBattleResult(String battleId) throws JsonProcessingException {
        logger.info("Fetching battle result for battleId: " + battleId);
        Optional<Battle> battleOptional = Optional.ofNullable(battleMapper.findByBattleId(battleId));
        if (battleOptional.isEmpty()) {
            logger.warn("Battle not found: " + battleId);
            throw new ResourceNotFoundException("Battle not found: " + battleId);
        }
        Battle battle = battleOptional.get();

        CharacterInstance challengerInstance = characterInstanceMapper.findByCharacterId(battle.getChallengerId());

        UserStats challengerStats = statsService.getUserStats(challengerInstance.getUserId());

        CharacterTemplate challengerTemplate = characterService.findByTemplateId(challengerInstance.getCharacterTemplateId());


        CharacterInstance defenderInstance = characterInstanceMapper.findByCharacterId(battle.getDefenderId());

        UserStats defenderStats = statsService.getUserStats(defenderInstance.getUserId());

        CharacterTemplate defenderTemplate = characterService.findByTemplateId(defenderInstance.getCharacterTemplateId());

        // Convert the JSON string log back to a List of objects
        List<BattleLogEntry> log = objectMapper.readValue(battle.getBattleLog(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BattleLogEntry.class));

        BattleResultDTO result = new BattleResultDTO();
        result.setPlayer(ConvertBattleCharacterUtil.toBattleCharacterDTO(imageBaseUrl, challengerInstance, challengerTemplate, challengerStats));
        result.setOpponent(ConvertBattleCharacterUtil.toBattleCharacterDTO(imageBaseUrl, defenderInstance, defenderTemplate, defenderStats));
        result.setWinnerId(battle.getWinnerId());
        result.setLog(log);

        logger.info("Fetched battle result: " + result);

        return result;
    }

    /**
     * Deletes battle logs older than the specified date asynchronously.
     *
     * @param date The cutoff date; battle logs older than this date will be deleted.
     */
    @Async
    public void deleteBattleLogsOlderThan(LocalDate date){
        logger.info("Deleting battles older than: " + date);
        battleMapper.deleteBattlesOlderThan(date);
        logger.info("Finished deleting battles older than: " + date);
    }

    /**
     * Calculates the damage dealt by the attacker to the defender.
     *
     * @param attacker The attacking character
     * @param defender The defending character
     * @return A {@link DamageResult} containing the damage amount and whether it was a critical hit
     */
    private DamageResult calculateDamage(BattleCharacterDTO attacker, BattleCharacterDTO defender) {
        boolean isCritical = false;
        double baseDamage = (attacker.getStats().getAtk() * 1.2) - (defender.getStats().getDef() * 0.5);
        double randomness = 0.9 + (random.nextDouble() * 0.2); // +/- 10% variance
        double finalDamage = baseDamage * randomness;

        // Check for a critical hit
        if (random.nextDouble() < GameConstant.CRITICAL_CHANCE) {
            isCritical = true;
            finalDamage *= GameConstant.CRITICAL_MULTIPLIER;
        }

        int calculatedDamage = Math.max(1, (int) Math.round(finalDamage));

        return new DamageResult(calculatedDamage, isCritical);
    }

    /**
     * Constructs a NotificationCreateDTO for the battle result notification.
     * It is to be sent to the opponent.
     *
     * @param dto      The {@link BattleChallengeDTO} containing challenger and opponent details
     * @param winnerId The ID of the winning character
     * @return The constructed {@link NotificationCreateDTO}
     */
    private static NotificationCreateDTO getNotificationCreateDTO(BattleChallengeDTO dto, String winnerId) {
        NotificationCreateDTO notification = new NotificationCreateDTO();
        boolean isOpponentWinner = winnerId.equals(dto.getOpponentCharacterId());
        notification.setUserId(dto.getOpponentUserId());
        notification.setNotificationType(NotificationType.BATTLE_CHALLENGE);
        notification.setTitle("Battle Completed");
        notification.setMessage(dto.getChallengerUsername() + " just challenged your character to a battle!" +
                (isOpponentWinner ? " You won!" : " You lost!"));
        return notification;
    }

}