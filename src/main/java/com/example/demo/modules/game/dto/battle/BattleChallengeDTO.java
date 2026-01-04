package com.example.demo.modules.game.dto.battle;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BattleChallengeDTO {

    @NotNull(message = "Opponent user ID cannot be null")
    private String opponentUserId;

    @NotNull(message = "Challenger username cannot be null")
    private String challengerUsername;

    @NotNull(message = "Challenger character ID cannot be null")
    private String challengerCharacterId;

    @NotNull(message = "Opponent character ID cannot be null")
    private String opponentCharacterId;
}
