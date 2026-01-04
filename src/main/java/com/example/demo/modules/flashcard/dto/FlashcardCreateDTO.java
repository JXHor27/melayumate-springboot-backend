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
public class FlashcardCreateDTO {
    @NotNull(message = "List ID cannot be null")
    private String flashcardListId;

    @NotBlank(message = "English word cannot be blank")
    @Size(max = 25, message = "English word cannot exceed 25 characters")
    private String englishWord;

    @NotBlank(message = "Malay word cannot be blank")
    @Size(max = 25, message = "Malay word cannot exceed 25 characters")
    private String malayWord;
}
