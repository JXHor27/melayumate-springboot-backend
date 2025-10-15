package com.example.demo.game.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BattleLogEntry {
    private int turn;
    private String attackerId;
    private String defenderId;
    private int damage;
    private String message;
    // Getters & Setters
}

