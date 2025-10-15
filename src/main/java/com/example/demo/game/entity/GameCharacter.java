package com.example.demo.game.entity;

import com.example.demo.enums.CharacterStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class GameCharacter {
    private String characterId;
    private String name;
    private String type;
    private String imageUrl;
    private int level;
    private int hp;
    private int attack;
    private int defense;
    private int speed;
    private String userId;
    private CharacterStatus status;
    private LocalDateTime listedAt;
}
