package com.example.demo.modules.speech.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TTSRequest {
    private String text;
    private String gender;
}
