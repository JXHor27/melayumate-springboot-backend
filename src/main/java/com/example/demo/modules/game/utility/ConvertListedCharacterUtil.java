package com.example.demo.modules.game.utility;

import com.example.demo.modules.dashboard.entity.UserStats;
import com.example.demo.modules.game.dto.character.ListedCharacterDTO;
import com.example.demo.modules.game.entity.CharacterInstance;
import com.example.demo.modules.game.entity.CharacterTemplate;

public class ConvertListedCharacterUtil {

    /**
     * Converts a CharacterInstance and its corresponding CharacterTemplate and UserStats
     * into a ListedCharacterDTO.
     *
     * @param imageBaseUrl      The base URL for character images.
     * @param character         The CharacterInstance to convert.
     * @param characterTemplate The CharacterTemplate associated with the character.
     * @param userStats         The UserStats containing the user's current level.
     * @return A {@link ListedCharacterDTO} representing the character for battle.
     */
    public static ListedCharacterDTO toListedCharacterDTO(String imageBaseUrl, CharacterInstance character, CharacterTemplate characterTemplate, UserStats userStats) {
        ListedCharacterDTO dto = new ListedCharacterDTO();
        dto.setCharacterId(character.getCharacterId());
        dto.setTemplateId(characterTemplate.getTemplateId());

        dto.setCharacterName(characterTemplate.getCharacterName());
        dto.setCharacterType(characterTemplate.getCharacterType());

        String fullImageUrl = imageBaseUrl + characterTemplate.getImageUrl();
        dto.setImageUrl(fullImageUrl);

        dto.setStats(CalculateStatsUtil.calculateStatsForLevel(characterTemplate, userStats.getCurrentLevel()));

        dto.setCharacterStatus(character.getCharacterStatus());
        dto.setListedAt(character.getListedAt());

        dto.setUserId(userStats.getUserId());
        dto.setUsername(userStats.getUsername());
        dto.setLevel(userStats.getCurrentLevel());
        return dto;
    }
}
