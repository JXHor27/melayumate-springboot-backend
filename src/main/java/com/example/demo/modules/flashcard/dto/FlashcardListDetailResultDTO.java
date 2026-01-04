package com.example.demo.modules.flashcard.dto;

import com.example.demo.enums.CardDefaultLanguage;
import com.example.demo.modules.flashcard.entity.Flashcard;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class FlashcardListDetailResultDTO {
    private String userId;
    private String flashcardListId;
    private String title;
    private String description;
    private int flashcardNumber;
    private boolean isRandom;
    private CardDefaultLanguage defaultLanguage;
    private List<Flashcard> cards;
}
