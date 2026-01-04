package com.example.demo.modules.flashcard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FlashcardListCreateDTO {
    @NotNull(message = "User ID cannot be null")
    private String userId;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 25, message = "Title cannot exceed 25 characters")
    private String title;

    @Size(max = 60, message = "Description cannot exceed 60 characters")
    private String description;
}
