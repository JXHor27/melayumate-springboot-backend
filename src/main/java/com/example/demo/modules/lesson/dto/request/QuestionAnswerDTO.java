package com.example.demo.modules.lesson.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QuestionAnswerDTO {

    @NotNull
    private String userId;

    @NotNull
    private String questionId;

    @NotNull
    private String lessonId;

    @NotBlank(message = "Answer cannot be blank")
    private String selectedAnswer;

    @NotNull
    private boolean isCorrect;
}
