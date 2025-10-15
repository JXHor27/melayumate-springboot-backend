package com.example.demo.flashcard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String englishWord;

    @NotBlank(message = "Malay word cannot be blank")
    private String malayWord;
}
