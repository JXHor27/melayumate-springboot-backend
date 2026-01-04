package com.example.demo.modules.game.dto.battle;

import com.example.demo.modules.game.dao.BattleLogEntry;
import com.example.demo.modules.game.dto.character.BattleCharacterDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;
@Getter
@Setter
@ToString
public class BattleResultDTO {
    private BattleCharacterDTO player;
    private BattleCharacterDTO opponent;
    private String winnerId;
    private List<BattleLogEntry> log;
}