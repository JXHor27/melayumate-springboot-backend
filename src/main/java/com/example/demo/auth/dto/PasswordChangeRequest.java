package com.example.demo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    @NotBlank(message = "Email cannot be blank")
    private String userId;

    @NotBlank(message = "Old password cannot be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$", message = "Password must be at least 8 characters long, include uppercase, lowercase, number, and special character.")
    private String oldPassword;

    @NotBlank(message = "New password cannot be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$", message = "Password must be at least 8 characters long, include uppercase, lowercase, number, and special character.")
    private String newPassword;

    @NotBlank(message = "Confirm password cannot be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$", message = "Password must be at least 8 characters long, include uppercase, lowercase, number, and special character.")
    private String confirmNewPassword;
}
