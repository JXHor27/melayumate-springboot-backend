package com.example.demo.modules.game.dto.character;


import com.example.demo.enums.CharacterStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class OwnedCharacterDTO extends BaseCharacterDTO {
    private CharacterStatus characterStatus;
    private Instant listedAt;
    private boolean isPrimary;
}
