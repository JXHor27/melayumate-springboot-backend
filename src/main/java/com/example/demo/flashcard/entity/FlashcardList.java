package com.example.demo.flashcard.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FlashcardList {
    private String userId;
    private String flashcardListId;
    private String title;
    private String description;
    private int flashcardNumber;
}
