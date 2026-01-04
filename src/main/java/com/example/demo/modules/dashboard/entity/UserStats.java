package com.example.demo.modules.dashboard.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserStats {
    private String userId;
    private String username;
    private int dailyGoal;
    private int currentLevel;
    private int currentExp;
}
