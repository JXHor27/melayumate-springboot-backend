package com.example.demo.modules.lesson.dto.result;

import com.example.demo.enums.QuestionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class QuestionProgressDTO {
    private String questionId;
    private String lessonId;
    private QuestionType questionType;
    private String promptText;
    private String attributes;
    private boolean isCompleted;
    private String selectedAnswer;
    private boolean isCorrect;

}
