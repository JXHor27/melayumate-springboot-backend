package com.example.demo.modules.lesson.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
public class MultipleChoiceRequest extends BaseQuestionRequest{

    @NotNull
    private List<String> options;

    @NotNull
    private int correctAnswerIndex;
}
