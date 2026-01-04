package com.example.demo.modules.dashboard.service;

import com.example.demo.auth.repo.UserMapper;
import com.example.demo.enums.NotificationType;
import com.example.demo.modules.dashboard.dto.NotificationCreateDTO;
import com.example.demo.modules.dashboard.entity.Notification;
import com.example.demo.modules.dashboard.repo.NotificationMapper;
import com.example.demo.modules.lesson.entity.Lesson;
import com.example.demo.service.IdGeneratorService;
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
    private final UserMapper userMapper;

    @Autowired
    private final NotificationMapper notificationMapper;

    @Autowired
    private final IdGeneratorService idGeneratorService;

    /**
     * Retrieves all unread notifications for a given user.
     *
     * @param userId The ID of the user whose unread notifications are to be fetched.
     * @return A list of unread {@link Notification} for the specified user.
     */
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

    /**
     * Creates a new notification asynchronously based on the provided DTO.
     *
     * @param dto The {@link NotificationCreateDTO} containing notification details.
     */
    @Async
    public void createNotification(NotificationCreateDTO dto) {
        try {
            logger.info("Creating new notification: " + dto);

            Notification notification = new Notification();
            notification.setNotificationId(idGeneratorService.generateNotificationId());
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

    /**
     * Marks all notifications as read for a specific user asynchronously.
     *
     * @param userId The ID of the user whose notifications are to be marked as read.
     */
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

    /**
     * Deletes notifications older than the specified date asynchronously.
     *
     * @param date The cutoff date; notifications older than this date will be deleted.
     */
    @Async
    public void deleteNotificationsOlderThan(LocalDate date){
        logger.info("Deleting notifications older than: " + date);
        notificationMapper.deleteNotificationsOlderThan(date);
        logger.info("Finished deleting notifications older than: " + date);
    }


    /**
     * Broadcasts a new lesson available notification to all users asynchronously.
     *
     * @param lesson The {@link Lesson} that is now available.
     */
    @Async
    public void broadcastNewLessonNotification(Lesson lesson) {
        try {
            logger.info("Broadcasting lesson notification: " + lesson);

            List<String> userIds = userMapper.findAllUserIds();

            for (String userId : userIds) {
                NotificationCreateDTO dto = new NotificationCreateDTO();
                dto.setUserId(userId);
                dto.setNotificationType(NotificationType.NEW_LESSON_AVAILABLE);
                dto.setTitle("New Lesson Available");
                dto.setMessage("A new lesson \"" + lesson.getTitle() + "\" is now available. Check it out!");
                createNotification(dto);
            }
            logger.info("Broadcasted lesson available notification");
        }
        catch (Exception e) {
            logger.error("Error during broadcasting notification.", e);
        }
    }
}
