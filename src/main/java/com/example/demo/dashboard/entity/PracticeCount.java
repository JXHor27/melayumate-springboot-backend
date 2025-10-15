package com.example.demo.dashboard.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PracticeCount {
    private int flashcardDone;
    private int dialogueDone;
    private int lessonDone;
}
