package com.example.demo.modules.lesson.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@ToString
public class ListeningRequest extends BaseQuestionRequest {

    @NotNull
    private String audioFile;

    @NotNull
    private List<String> options;

    @NotNull
    private int correctAnswerIndex;
}
