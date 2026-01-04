package com.example.demo.modules.game.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DamageResult {
    private int damage;
    private boolean isCritical;
}
