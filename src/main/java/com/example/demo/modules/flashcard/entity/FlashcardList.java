package com.example.demo.modules.flashcard.entity;

import com.example.demo.enums.CardDefaultLanguage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class FlashcardList {
    private String userId;
    private String flashcardListId;
    private String title;
    private String description;
    private int flashcardNumber;
    private boolean isRandom;
    private CardDefaultLanguage defaultLanguage;
    private LocalDate nextReviewDate;
    private int currentStreak;
}
