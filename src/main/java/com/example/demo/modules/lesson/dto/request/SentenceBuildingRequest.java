package com.example.demo.modules.lesson.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
public class SentenceBuildingRequest extends BaseQuestionRequest {

    @NotBlank
    @Size(min = 2, message = "Must provide at least two words")
    private List<String> words;

    @NotBlank
    private String correctSentence;
}
