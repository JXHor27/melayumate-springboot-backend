package com.example.demo.dashboard.service;

import com.example.demo.dashboard.dto.NotificationCreateDTO;
import com.example.demo.dashboard.entity.Notification;
import com.example.demo.dashboard.repo.NotificationMapper;
import com.example.demo.id.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final Log logger = LogFactory.getLog(NotificationService.class);

    @Autowired
    private final NotificationMapper notificationMapper;

    @Autowired
    private final IdGenerator idGenerator;

    public List<Notification> getAllUnreadNotifications(String userId){
        logger.info("Fetching notifications for user: " + userId);

        List<Notification> notifications = notificationMapper.getUnreadNotificationsByUserId(userId);

        if (notifications.isEmpty()) {
            logger.info("No notifications found with userId: " + userId);
            return notifications;
        }

        logger.info("Fetched notifications: " + notifications);
        return notifications;
    }

    @Async
    public void createNotification(NotificationCreateDTO dto) {
        try {
            logger.info("Creating new notification: " + dto);

            Notification notification = new Notification();
            notification.setNotificationId(idGenerator.generateNotificationId());
            notification.setUserId(dto.getUserId());
            notification.setNotificationType(dto.getNotificationType());
            notification.setTitle(dto.getTitle());
            notification.setMessage(dto.getMessage());
            notification.setRead(false);
            notification.setCreatedAt(Instant.now());

            notificationMapper.createNotification(notification);
            logger.info("Created notification: " + notification);
        }
        catch (Exception e) {
            logger.error("Error during creating notification.", e);
        }
    }

    public void markNotificationsAsRead(String userId){
        try {
            logger.info("Marking notifications as read for user: " + userId);

            notificationMapper.markNotificationsAsRead(userId);

            logger.info("Notifications marked as read for user: " + userId);
        }
        catch (Exception e) {
            logger.error("Error during marking notifications as read.", e);
        }
    }

    @Async
    public void deleteNotificationsOlderThan(LocalDate date){
        logger.info("Deleting notifications older than: " + date);
        notificationMapper.deleteNotificationsOlderThan(date);
        logger.info("Finished deleting notifications older than: " + date);

    }
}
