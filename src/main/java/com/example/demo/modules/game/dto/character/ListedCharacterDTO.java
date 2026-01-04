package com.example.demo.modules.game.dto.character;

import com.example.demo.enums.CharacterStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString(callSuper = true)
public class ListedCharacterDTO extends BaseCharacterDTO {
    private CharacterStatus characterStatus;
    private Instant listedAt;
    private String userId;
    private String username;
    private int level;
}
