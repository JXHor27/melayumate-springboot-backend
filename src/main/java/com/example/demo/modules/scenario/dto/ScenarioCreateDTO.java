package com.example.demo.modules.scenario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScenarioCreateDTO {
    @NotNull(message = "User ID cannot be null")
    private String userId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String description;
}
