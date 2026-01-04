package com.example.demo.modules.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// Represents a row in the 'character_templates' table
@Getter
@Setter
@ToString
public class CharacterTemplate {
    private String templateId;
    private String characterName;
    private String characterType;
    private String imageUrl;
    private int unlockLevel;
    private int baseHp;
    private int baseAttack;
    private int baseDefense;
    private int baseSpeed;
}
