package com.example.demo.modules.game.utility;

import com.example.demo.modules.dashboard.entity.UserStats;
import com.example.demo.modules.game.dto.character.BattleCharacterDTO;
import com.example.demo.modules.game.entity.CharacterInstance;
import com.example.demo.modules.game.entity.CharacterTemplate;

public class ConvertBattleCharacterUtil {

    /**
     * Converts a CharacterInstance and its corresponding CharacterTemplate and UserStats
     * into a BattleCharacterDTO.
     *
     * @param imageBaseUrl      The base URL for character images.
     * @param character         The CharacterInstance to convert.
     * @param characterTemplate The CharacterTemplate associated with the character.
     * @param userStats         The UserStats containing the user's current level.
     * @return A {@link BattleCharacterDTO} representing the character for battle.
     */
    public static BattleCharacterDTO toBattleCharacterDTO(String imageBaseUrl, CharacterInstance character, CharacterTemplate characterTemplate, UserStats userStats) {
        BattleCharacterDTO dto = new BattleCharacterDTO();
        dto.setCharacterId(character.getCharacterId());
        dto.setTemplateId(characterTemplate.getTemplateId());

        dto.setCharacterName(characterTemplate.getCharacterName());
        dto.setCharacterType(characterTemplate.getCharacterType());

        String fullImageUrl = imageBaseUrl + characterTemplate.getImageUrl();
        dto.setImageUrl(fullImageUrl);

        dto.setStats(CalculateStatsUtil.calculateStatsForLevel(characterTemplate, userStats.getCurrentLevel()));

        dto.setLevel(userStats.getCurrentLevel());
        return dto;
    }
}
