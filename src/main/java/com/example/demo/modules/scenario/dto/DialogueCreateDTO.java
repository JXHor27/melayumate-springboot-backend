package com.example.demo.modules.scenario.dto;

import com.example.demo.enums.DialogueType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DialogueCreateDTO {
    @NotNull(message = "Scenario ID cannot be null")
    private String scenarioId;

    @NotNull(message = "Dialogue type cannot be null")
    private DialogueType dialogueType;

    @NotBlank(message = "English word cannot be blank")
    private String english;

    @NotBlank(message = "Malay word cannot be blank")
    private String malay;

    @NotNull(message = "Dialogue order cannot be null")
    @Min(value = 1, message = "Dialogue order must be at least 1")
    @Max(value = 5, message = "Dialogue order must be at most 5")
    private int dialogueOrder;

    private String audioUrl;
}
