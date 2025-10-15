package com.example.demo.game.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;
@Getter
@Setter
@ToString
public class BattleResultDTO {
    // We use CharacterDTO to avoid sending the full User object
    private CharacterDTO player;
    private CharacterDTO opponent;
    private String winnerId;
    private List<BattleLogEntry> log;
    // Getters & Setters
}