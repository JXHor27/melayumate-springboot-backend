package com.example.demo.modules.dashboard.repo;

import com.example.demo.modules.dashboard.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface NotificationMapper {
    @Insert("INSERT INTO notifications(notification_id, user_id, notification_type, title, message, is_read, created_at) VALUES(#{notificationId}, #{userId}, #{notificationType}, #{title}, #{message}, #{isRead}, #{createdAt})")
    void createNotification(Notification notification);

    @Update("UPDATE notifications SET is_read = TRUE WHERE user_id = #{userId} AND is_read = FALSE")
    void markNotificationsAsRead(String userId);

    @Select("SELECT * FROM notifications WHERE user_id = #{userId} AND is_read = FALSE ORDER BY created_at DESC")
    @Results({
            @Result(property = "notificationId", column = "notification_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "notificationType", column = "notification_type"),
            @Result(property = "isRead", column = "is_read"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<Notification> getUnreadNotificationsByUserId(String userId);

    @Delete("DELETE FROM notifications WHERE DATE(created_at) < #{date}")
    void deleteNotificationsOlderThan(LocalDate date);
}
