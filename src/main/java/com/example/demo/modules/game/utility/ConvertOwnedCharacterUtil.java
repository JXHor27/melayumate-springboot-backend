package com.example.demo.modules.game.utility;

import com.example.demo.modules.dashboard.entity.UserStats;
import com.example.demo.modules.game.dto.character.OwnedCharacterDTO;
import com.example.demo.modules.game.entity.CharacterInstance;
import com.example.demo.modules.game.entity.CharacterTemplate;

public class ConvertOwnedCharacterUtil {

    /**
     * Converts a CharacterInstance and its corresponding CharacterTemplate and UserStats
     * into a OwnedCharacterDTO.
     *
     * @param imageBaseUrl      The base URL for character images.
     * @param character         The CharacterInstance to convert.
     * @param characterTemplate The CharacterTemplate associated with the character.
     * @param userStats         The UserStats containing the user's current level.
     * @return A {@link OwnedCharacterDTO} representing the character for battle.
     */
    public static OwnedCharacterDTO toOwnedCharacterDTO(String imageBaseUrl, CharacterInstance character, CharacterTemplate characterTemplate, UserStats userStats) {
        OwnedCharacterDTO dto = new OwnedCharacterDTO();
        dto.setCharacterId(character.getCharacterId());
        dto.setTemplateId(characterTemplate.getTemplateId());

        dto.setCharacterName(characterTemplate.getCharacterName());
        dto.setCharacterType(characterTemplate.getCharacterType());

        String fullImageUrl = imageBaseUrl + characterTemplate.getImageUrl();
        dto.setImageUrl(fullImageUrl);

        dto.setPrimary(character.isPrimary());

        dto.setStats(CalculateStatsUtil.calculateStatsForLevel(characterTemplate, userStats.getCurrentLevel()));

        dto.setCharacterStatus(character.getCharacterStatus());
        dto.setListedAt(character.getListedAt());
        return dto;
    }

}
