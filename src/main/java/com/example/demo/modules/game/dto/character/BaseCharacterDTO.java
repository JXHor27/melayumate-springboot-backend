package com.example.demo.modules.game.dto.character;

import com.example.demo.modules.game.dao.CalculatedStats;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseCharacterDTO {
    private String characterId; // The ID from the 'characters' table
    private String templateId; // The ID from the 'character_templates' table
    private String characterName;
    private String characterType;
    private String imageUrl;
    private CalculatedStats stats;
}
