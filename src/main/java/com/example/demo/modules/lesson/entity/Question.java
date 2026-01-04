package com.example.demo.modules.lesson.entity;

import com.example.demo.enums.QuestionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class Question {
    private String questionId;
    private String lessonId;
    private QuestionType questionType;
    private String promptText;
    private String attributes;    // Stored as a JSON string
    private Instant createdAt;
}
