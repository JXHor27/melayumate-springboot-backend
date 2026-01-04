package com.example.demo.modules.flashcard.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Flashcard {
    private String flashcardId;
    private String flashcardListId;
    private String englishWord;
    private String malayWord;
}
