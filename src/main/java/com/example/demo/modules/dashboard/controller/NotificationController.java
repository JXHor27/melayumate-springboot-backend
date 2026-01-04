package com.example.demo.modules.dashboard.controller;

import com.example.demo.modules.dashboard.entity.Notification;
import com.example.demo.modules.dashboard.entity.UserStats;
import com.example.demo.modules.dashboard.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @Autowired
    private final NotificationService notificationService;

    /**
     * Get all unread notifications by user ID.
     *
     * @param userId the ID of the user
     * @return a ResponseEntity containing the list of unread notifications
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getAllUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Marks all unread notifications of the user as read.
     *
     * @param userId the ID of the user
     * @return a ResponseEntity with HTTP status NO_CONTENT
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<UserStats> markNotificationsAsRead(@PathVariable String userId) {
        notificationService.markNotificationsAsRead(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
