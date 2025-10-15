package com.example.demo.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Battle {
    private String battleId;
    private GameCharacter challenger;
    private GameCharacter defender;
    private GameCharacter winner;
    private String battleLog; // Stored as a JSON string
    private LocalDateTime createdAt;

    // Constructors, Getters, Setters...
}