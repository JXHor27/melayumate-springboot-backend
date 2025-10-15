package com.example.demo.game.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CharacterDTO {
    private String id;
    private String name;
    private String type;
    private int level;
    private int maxHp; // Corresponds to the character's HP stat
    private String imageUrl;
    // Getters & Setters
}