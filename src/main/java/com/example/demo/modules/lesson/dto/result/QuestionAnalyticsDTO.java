package com.example.demo.modules.lesson.dto.result;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class QuestionAnalyticsDTO {
    private int totalAttempts;
    private int correctAttempts;

    // This will only be populated for MULTIPLE_CHOICE or LISTENING questions
    private List<AnswerDistribution> answerDistribution;
}
