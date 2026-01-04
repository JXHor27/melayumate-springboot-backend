package com.example.demo.modules.game.service;

import com.example.demo.modules.dashboard.entity.UserStats;
import com.example.demo.modules.dashboard.service.StatsService;
import com.example.demo.enums.CharacterStatus;
import com.example.demo.exception.CharacterRuleException;
import com.example.demo.modules.game.dto.character.ListedCharacterDTO;
import com.example.demo.modules.game.entity.CharacterInstance;
import com.example.demo.modules.game.entity.CharacterTemplate;
import com.example.demo.modules.game.repo.CharacterInstanceMapper;
import com.example.demo.modules.game.repo.CharacterTemplateMapper;
import com.example.demo.modules.game.utility.ConvertListedCharacterUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BattleLobbyService {

    private static final Log logger = LogFactory.getLog(BattleLobbyService.class);

    @Autowired
    private final StatsService statsService;

    @Autowired
    private final CharacterInstanceMapper characterInstanceMapper;

    // inject the service, instead of direct repository for caching capability
    @Autowired
    private final CharacterService characterService;

    @Value("${aws.s3.base-url}")
    private String imageBaseUrl;

    /**
     * Lists the primary character of the user for battle.
     *
     * @param userId      The ID of the user.
     * @param characterId The ID of the character to be listed.
     * @throws CharacterRuleException if the user has no primary character.
     */
    @Transactional
    public void listPrimaryCharacterForBattle(String userId, String characterId) {
        logger.info("User " + userId + " is listing character " + characterId + " for battle.");
        CharacterInstance primaryCharacter = characterInstanceMapper.findPrimaryCharacter(userId);

        if (primaryCharacter == null) {
            logger.error("User has no primary character to list for battle: " + userId);
            throw new CharacterRuleException("User has no primary character to list for battle.");
        }

        primaryCharacter.setCharacterStatus(CharacterStatus.LISTED_FOR_BATTLE);
        primaryCharacter.setListedAt(Instant.now());
        logger.info("Setting character " + characterId + " status to LISTED_FOR_BATTLE.");
        characterInstanceMapper.updateStatus(primaryCharacter);
        logger.info("Character " + characterId + " listed for battle by user " + userId + ".");
    }

    /**
     * Unlists the primary character of the user from battle.
     *
     * @param userId      The ID of the user.
     * @param characterId The ID of the character to be unlisted.
     * @throws CharacterRuleException if the user has no primary character.
     */
    @Transactional
    public void unlistPrimaryCharacter(String userId, String characterId) {

        CharacterInstance primaryCharacter = characterInstanceMapper.findPrimaryCharacter(userId);

        if (primaryCharacter == null) {
            logger.error("User has no primary character to list for battle: " + userId);
            throw new CharacterRuleException("User has no primary character to list for battle.");
        }

        primaryCharacter.setCharacterStatus(CharacterStatus.IDLE);
        primaryCharacter.setListedAt(null);
        characterInstanceMapper.updateStatus(primaryCharacter);
        logger.info("Character " + characterId + " unlisted for battle by user " + userId + ".");
    }

    /**
     * Retrieves a list of listed challengers excluding the current user.
     *
     * @param currentUserId The ID of the current user.
     * @return A list of {@link ListedCharacterDTO} representing the listed challengers.
     */
    public List<ListedCharacterDTO> getListedChallengers(String currentUserId) {
        logger.info("Finding listed challenger: " + currentUserId);

        List<CharacterInstance> challengers = characterInstanceMapper.findListedChallengers(currentUserId);
        List<ListedCharacterDTO> listedChallengers = new ArrayList<>();

        // Transform each CharacterInstance (fetched from database) to ListedCharacterDTO
        for (CharacterInstance challenger : challengers) {

            UserStats challengerStats = statsService.getUserStats(challenger.getUserId());
            CharacterTemplate characterTemplate = characterService.findByTemplateId(challenger.getCharacterTemplateId());

            ListedCharacterDTO dto = ConvertListedCharacterUtil.toListedCharacterDTO(imageBaseUrl, challenger, characterTemplate, challengerStats);
            listedChallengers.add(dto);
        }
        return listedChallengers;
    }

}
