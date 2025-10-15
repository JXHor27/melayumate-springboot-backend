package com.example.demo.dashboard.dto;

import com.example.demo.enums.PracticeType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PracticeCreateDTO {
    @NotNull(message = "User ID cannot be null")
    private String userId;

    @NotNull(message = "Practice type cannot be null")
    private PracticeType practiceType;
}
