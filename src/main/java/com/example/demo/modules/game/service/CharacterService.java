package com.example.demo.modules.game.service;

import com.example.demo.modules.dashboard.entity.UserStats;
import com.example.demo.modules.dashboard.service.StatsService;
import com.example.demo.enums.CharacterStatus;
import com.example.demo.exception.CharacterRuleException;
import com.example.demo.modules.game.dto.character.OwnedCharacterDTO;
import com.example.demo.modules.game.entity.CharacterInstance;
import com.example.demo.modules.game.entity.CharacterTemplate;
import com.example.demo.modules.game.repo.CharacterInstanceMapper;
import com.example.demo.modules.game.repo.CharacterTemplateMapper;
import com.example.demo.modules.game.utility.ConvertOwnedCharacterUtil;
import com.example.demo.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private static final Log logger = LogFactory.getLog(CharacterService.class);

    @Autowired
    private final StatsService statsService;

    @Autowired
    private final CharacterInstanceMapper characterInstanceMapper;

    @Autowired
    private final CharacterTemplateMapper characterTemplateMapper;

    @Autowired
    private final IdGeneratorService idGeneratorService;

    @Value("${aws.s3.base-url}")
    private String imageBaseUrl;

    /**
     * Retrieves all available character templates.
     * <p>
     * The result of this method is cached under the "characterTemplates" cache to
     * improve performance on subsequent calls.
     *
     * @return A list of all {@link CharacterTemplate} objects.
     */
    @Cacheable(value = "characterTemplates", key = "#root.methodName")
    public List<CharacterTemplate> findAllCharacterTemplates() {
        logger.info("Fetching all unlockable characters");

        List<CharacterTemplate> characterTemplates = characterTemplateMapper.findAllCharacterTemplates();
        for (CharacterTemplate characterTemplate : characterTemplates) {
            // Construct full image url from AWS S3
            String fullImageUrl = imageBaseUrl + characterTemplate.getImageUrl();
            characterTemplate.setImageUrl(fullImageUrl);
        }
        logger.info("Fetched unlockable characters: " + characterTemplates);
        return characterTemplates;
    }

    /**
     * Retrieves a single character template by its unique template ID.
     * <p>
     * The result of this method is cached under the "characterTemplate" cache to
     * improve performance on subsequent lookups for the same ID.
     *
     * @param templateId The unique identifier for the character template.
     * @return The {@link CharacterTemplate} object, or null if not found.
     */
    @Cacheable(value = "characterTemplate", key = "#templateId")
    public CharacterTemplate findByTemplateId(String templateId) {
        logger.info("Fetching character template with id: " + templateId);
        CharacterTemplate characterTemplate = characterTemplateMapper.findById(templateId);
        logger.info("Fetched unlockable characters: " + characterTemplate);
        return characterTemplate;
    }


    /**
     * Retrieves a list of characters owned by the specified user.
     *
     * @param userId The ID of the user whose owned characters are to be fetched.
     * @return A list of {@link OwnedCharacterDTO} representing the user's owned characters.
     */
    public List<OwnedCharacterDTO> findOwnedCharacters(String userId) {
        logger.info("Fetching owned characters for user: " + userId);
        UserStats userStats = statsService.getUserStats(userId);
        List<OwnedCharacterDTO> ownedCharacters = new ArrayList<>();
        List<CharacterInstance> characterInstances = characterInstanceMapper.findOwnedCharacters(userId);
        if (characterInstances.isEmpty()) {
            logger.info("No owned characters found for user: " + userId);
            return ownedCharacters;
        }
        // Transform each CharacterInstance (fetched from database) to OwnedCharacterDTO
        for (CharacterInstance characterInstance : characterInstances) {
            CharacterTemplate characterTemplate = characterTemplateMapper.findById(characterInstance.getCharacterTemplateId());
            OwnedCharacterDTO dto = ConvertOwnedCharacterUtil.toOwnedCharacterDTO(imageBaseUrl, characterInstance, characterTemplate, userStats);
            ownedCharacters.add(dto);
        }
        logger.info("Fetched owned characters: " + ownedCharacters);
        return ownedCharacters;
    }

    /**
     * Retrieves the primary character owned by the specified user.
     *
     * @param userId The ID of the user whose primary character is to be fetched.
     * @return An {@link OwnedCharacterDTO} representing the user's primary character, or null if not found.
     */
    public OwnedCharacterDTO findPrimaryCharacter(String userId) {
        logger.info("Fetching primary character for user: " + userId);
        UserStats userStats = statsService.getUserStats(userId);
        CharacterInstance characterInstance = characterInstanceMapper.findPrimaryCharacter(userId);
        if (characterInstance == null) {
            logger.info("No primary character found for user: " + userId);
            return new OwnedCharacterDTO();
        }
        CharacterTemplate characterTemplate = characterTemplateMapper.findById(characterInstance.getCharacterTemplateId());
        OwnedCharacterDTO primaryCharacter = ConvertOwnedCharacterUtil.toOwnedCharacterDTO(imageBaseUrl, characterInstance, characterTemplate, userStats);
        logger.info("Fetched primary character: " + primaryCharacter);
        return primaryCharacter;
    }

    /**
     * Acquires a new character for the specified user based on the provided template ID.
     *
     * @param userId     The ID of the user acquiring the character.
     * @param templateId The ID of the character template to be acquired.
     * @throws CharacterRuleException if any rule violations occur during acquisition.
     */
    @Transactional
    public void acquireCharacter(String userId, String templateId) {
        logger.info("Acquiring character template: " + templateId + " for user: " + userId);
        UserStats userStats = statsService.getUserStats(userId);
        CharacterTemplate template = characterTemplateMapper.findById(templateId);
        int ownedCount = characterInstanceMapper.countByUserId(userId);
        // Rule Enforcement
        if (template == null) {
            logger.error("Character template with ID " + templateId + " not found.");
            throw new CharacterRuleException("Character template not found.");
        }
        if (ownedCount >= 2) {
            logger.error("User " + userId + " already owns the maximum number of characters.");
            throw new CharacterRuleException("User already owns the maximum number of characters.");
        }
        if (userStats.getCurrentLevel() < template.getUnlockLevel()){
            logger.error("User " + userId + " level is too low to unlock character " + templateId + ".");
            throw new CharacterRuleException("User level is too low to unlock this character.");
        }
        CharacterInstance newInstance = new CharacterInstance();
        newInstance.setCharacterId(idGeneratorService.generateCharacterId());
        newInstance.setUserId(userId);
        newInstance.setCharacterTemplateId(templateId);
        newInstance.setUnlockedAt(Instant.now());
        // If this is the user's VERY FIRST character, automatically set it as primary.
        newInstance.setPrimary(ownedCount == 0);
        newInstance.setCharacterStatus(CharacterStatus.IDLE); // default IDLE
        newInstance.setListedAt(null); // not listed yet
        characterInstanceMapper.insert(newInstance);
        logger.info("Successfully acquired character: " + newInstance);
    }

    /**
     * Sets the specified character as the primary character for the user.
     *
     * @param userId      The ID of the user.
     * @param characterId The ID of the character to be set as primary.
     * @throws CharacterRuleException if any rule violations occur during the operation.
     */
    @Transactional
    public void  setPrimaryCharacter(String userId, String characterId) {
        logger.info("Request to set character " + characterId + " as primary for user " + userId + ".");
        CharacterInstance instance = characterInstanceMapper.findByCharacterId(characterId);
        if (instance == null || !instance.getUserId().equals(userId)) {
            logger.error("User " + userId + " does not own character " + characterId + ".");
            throw new CharacterRuleException("User does not own this character.");
        }
        if (instance.isPrimary()){
            logger.error("Character " + characterId + " is already the primary character for user " + userId + ".");
            throw new CharacterRuleException("Character is already the primary character.");
        }
        // Need to update both CharacterInstance for consistency, like a switch operation
        characterInstanceMapper.setPrimaryCharacter(characterId);
        characterInstanceMapper.setSecondaryCharacter(characterId, userId);
    }
}
