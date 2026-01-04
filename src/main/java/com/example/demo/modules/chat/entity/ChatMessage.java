package com.example.demo.modules.chat.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class ChatMessage {
    private String chatId;
    private String userId;
    private String avatar;
    private String username;
    private int currentLevel;
    private String message;
    private Instant sentAt;
}
