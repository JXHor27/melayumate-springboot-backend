package com.example.demo.game.service;

import com.example.demo.enums.CharacterStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.game.dto.BattleLogEntry;
import com.example.demo.game.dto.BattleResultDTO;
import com.example.demo.game.dto.CharacterDTO;
import com.example.demo.game.entity.Battle;
import com.example.demo.game.repo.BattleMapper;
import com.example.demo.game.repo.CharacterMapper;
import com.fasterxml.jackson.databind.ObjectMapper; // You'll need the Jackson library
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import com.example.demo.game.entity.GameCharacter;
@Service
@RequiredArgsConstructor
public class BattleService {

    private static final Log logger = LogFactory.getLog(BattleService.class);


    @Autowired
    private final CharacterMapper characterMapper;

    @Autowired
    private final BattleMapper battleMapper;

    @Autowired
    private final ObjectMapper objectMapper; // For converting the log to/from JSON string



    // --- BATTLE SIMULATION LOGIC ---
    public Battle createAndSimulateBattle(String challengerId, String defenderId) throws Exception {
        Optional<GameCharacter> challengerOptional = Optional.ofNullable(characterMapper.findById(challengerId));
        if (challengerOptional.isEmpty()) {
            logger.warn("Challenger does not exist: " + challengerId);
            throw new ResourceNotFoundException("Challenger does not exist: " + challengerId);
        }
        GameCharacter challenger = challengerOptional.get();

        Optional<GameCharacter> defenderOptional = Optional.ofNullable(characterMapper.findById(defenderId));
        if (defenderOptional.isEmpty()) {
            logger.warn("Defender does not exist: " + challengerId);
            throw new ResourceNotFoundException("Defender does not exist: " + challengerId);
        }
        GameCharacter defender = challengerOptional.get();


        int challengerCurrentHp = challenger.getHp();
        int defenderCurrentHp = defender.getHp();
        List<BattleLogEntry> battleLog = new ArrayList<>();
        int turnCounter = 1;

        GameCharacter currentAttacker = challenger.getSpeed() >= defender.getSpeed() ? challenger : defender;
        GameCharacter currentDefender = currentAttacker.getCharacterId().equals(challenger.getCharacterId()) ? defender : challenger;

        while (challengerCurrentHp > 0 && defenderCurrentHp > 0) {
            int damage = calculateDamage(currentAttacker, currentDefender);

            if (currentDefender.getCharacterId().equals(challenger.getCharacterId())) {
                challengerCurrentHp -= damage;
            } else {
                defenderCurrentHp -= damage;
            }

            BattleLogEntry entry = new BattleLogEntry();
            entry.setTurn(turnCounter++);
            entry.setAttackerId(currentAttacker.getCharacterId());
            entry.setDefenderId(currentDefender.getCharacterId());
            entry.setDamage(damage);
            entry.setMessage(currentAttacker.getName() + " attacks!");
            battleLog.add(entry);

            // Swap roles for the next turn
            GameCharacter temp = currentAttacker;
            currentAttacker = currentDefender;
            currentDefender = temp;
        }

        // Determine winner and save the battle
        GameCharacter winner = challengerCurrentHp > 0 ? challenger : defender;

        Battle battle = new Battle();
        battle.setChallenger(challenger);
        battle.setDefender(defender);
        battle.setWinner(winner);
        // Convert the log List to a JSON string for storage
        String logAsString = objectMapper.writeValueAsString(battleLog);
        battle.setBattleLog(logAsString);
        battleMapper.insert(battle);

        // Reset characters' status after battle
        challenger.setStatus(CharacterStatus.IDLE);
        defender.setStatus(CharacterStatus.IDLE);
        characterMapper.update(challenger);
        characterMapper.update(defender);

        return battle;
    }

    private int calculateDamage(GameCharacter attacker, GameCharacter defender) {
        double baseDamage = (attacker.getAttack() * 0.8) - (defender.getDefense() * 0.5);
        double randomness = 0.9 + (new Random().nextDouble() * 0.2); // Random float between 0.9 and 1.1
        double finalDamage = baseDamage * randomness;
        return Math.max(1, (int) Math.round(finalDamage));
    }

    // --- METHOD TO CREATE THE BATTLE RESULT DTO ---
    public BattleResultDTO getBattleResult(Long battleId, Long currentUserId) throws Exception {
        Optional<Battle> battleOptional = Optional.ofNullable(battleMapper.findById(battleId));
        if (battleOptional.isEmpty()) {
            logger.warn("Battle not found: " + battleId);
            throw new ResourceNotFoundException("Battle not found: " + battleId);
        }
        Battle battle = battleOptional.get();

        GameCharacter userCharacter = battle.getChallenger().getUserId().equals(currentUserId)
                ? battle.getChallenger()
                : battle.getDefender();

        GameCharacter opponentCharacter = userCharacter.getUserId().equals(battle.getChallenger().getUserId())
                ? battle.getDefender()
                : battle.getChallenger();

        // Convert the JSON string log back to a List of objects
        List<BattleLogEntry> log = objectMapper.readValue(battle.getBattleLog(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BattleLogEntry.class));

        BattleResultDTO result = new BattleResultDTO();
        result.setPlayer(toCharacterDTO(userCharacter));
        result.setOpponent(toCharacterDTO(opponentCharacter));
        result.setWinnerId(battle.getWinner().getCharacterId());
        result.setLog(log);

        return result;
    }

    // Helper to convert Entity to DTO
    private CharacterDTO toCharacterDTO(GameCharacter character) {
        CharacterDTO dto = new CharacterDTO();
        dto.setId(character.getCharacterId());
        dto.setName(character.getName());
        dto.setType(character.getType());
        dto.setLevel(character.getLevel());
        dto.setMaxHp(character.getHp());
        dto.setImageUrl(character.getImageUrl());
        return dto;
    }
}