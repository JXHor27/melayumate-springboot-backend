package com.example.demo.ml.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TTSRequest {
    private String text;
    private String gender;
}
