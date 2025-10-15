package com.example.demo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyCodeRequest {
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Code cannot be blank")
    private String code;
}
