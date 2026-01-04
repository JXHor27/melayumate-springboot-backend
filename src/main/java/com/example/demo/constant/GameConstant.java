package com.example.demo.constant;

import java.util.List;

public final class GameConstant {

    private GameConstant() {

    }

    public static final int PRACTICE_EXP = 10;
    public static final int BATTLE_WIN_EXP = 5;

    public static final double CRITICAL_CHANCE = 0.25; // 25% chance for a critical hit
    public static final double CRITICAL_MULTIPLIER = 2; // Critical hits deal 100% more damage

    public static final List<String> NORMAL_ATTACK_MESSAGES = List.of(
            "%s attacks! Damage: %d",
            "%s strikes! Damage: %d",
            "%s lunges forward! Damage: %d",
            "%s lands a blow. Damage: %d"
    );

    public static final List<String> CRITICAL_ATTACK_MESSAGES = List.of(
            "A critical hit! %s lands a devastating blow! Damage: %d",
            "Incredible! %s strikes a critical point! Damage: %d",
            "A critical strike from %s! Damage: %d",
            "Right on target! A critical hit by %s! Damage: %d"
    );
}
