package com.example.demo.modules.game.dto.character;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class BattleCharacterDTO extends BaseCharacterDTO {
    private int level;
}
