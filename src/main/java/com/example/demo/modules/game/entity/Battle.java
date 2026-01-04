package com.example.demo.modules.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class Battle {
    private String battleId;
    private String challengerId;
    private String defenderId;
    private String winnerId;
    private String battleLog; // Stored as a JSON string
    private Instant createdAt;
}