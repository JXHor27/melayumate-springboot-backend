package com.example.demo.dashboard.dto;

import com.example.demo.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NotificationCreateDTO {
    @NotNull(message = "User ID cannot be null")
    private String userId;

    @NotNull(message = "Notification type cannot be null")
    private NotificationType notificationType;

    @NotNull(message = "Title cannot be null")
    private String title;

    @NotNull(message = "Message cannot be null")
    private String message;
}
