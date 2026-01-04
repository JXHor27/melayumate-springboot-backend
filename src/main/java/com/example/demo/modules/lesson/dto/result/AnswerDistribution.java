package com.example.demo.modules.lesson.dto.result;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AnswerDistribution {
    private String answerOption;
    private int count;
}
