package com.example.demo.modules.game.entity;

import com.example.demo.enums.CharacterStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;

// Represents a row in the 'characters' table (renamed for clarity)
@Getter
@Setter
@ToString
public class CharacterInstance {
    private String characterId;
    private String userId;
    private String characterTemplateId;
    private Instant unlockedAt;
    private boolean isPrimary;
    private CharacterStatus characterStatus;
    private Instant listedAt;
}