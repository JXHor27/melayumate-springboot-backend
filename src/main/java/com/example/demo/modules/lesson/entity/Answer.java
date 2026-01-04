package com.example.demo.modules.lesson.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class Answer {
    private String answerId;
    private String userId;
    private String questionId;
    private String lessonId;
    private String selectedAnswer;
    private boolean isCorrect;
    private Instant answeredAt;
}
