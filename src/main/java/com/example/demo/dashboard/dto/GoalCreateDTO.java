package com.example.demo.dashboard.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoalCreateDTO {
    @NotNull(message = "User ID cannot be null")
    private String userId;

    @NotNull(message = "Daily goal cannot be null")
    @Min(value = 0, message = "Daily goal must be at least 0")
    private int dailyGoal;
}
