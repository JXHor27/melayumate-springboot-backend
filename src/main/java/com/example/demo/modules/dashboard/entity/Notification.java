package com.example.demo.modules.dashboard.entity;

import com.example.demo.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class Notification {
    private String notificationId;
    private String userId;
    private NotificationType notificationType;
    private String title;
    private String message;
    private boolean isRead;
    private Instant createdAt;
}
