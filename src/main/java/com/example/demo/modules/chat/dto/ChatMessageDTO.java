package com.example.demo.modules.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessageDTO {
    @NotNull(message = "User ID cannot be null")
    private String userId;

    @NotNull(message = "Username cannot be null")
    private String username;

    @NotNull(message = "Level cannot be null")
    private int currentLevel;

    @NotNull(message = "Avatar URL cannot be null")
    private String avatar;

    @NotBlank(message = "Message cannot be blank")
    private String message;
}
