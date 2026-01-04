package com.example.demo.scheduler;

import com.example.demo.modules.chat.service.ChatService;
import com.example.demo.modules.dashboard.service.NotificationService;
import com.example.demo.modules.dashboard.service.PracticeSessionService;
import com.example.demo.modules.game.service.BattleService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class DataRetentionScheduler {

    private static final Log logger = LogFactory.getLog(DataRetentionScheduler.class);

    @Autowired
    private final PracticeSessionService practiceSessionService;

    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final BattleService battleService;

    @Autowired
    private final ChatService chatService;

    /**
     * This task runs every day at 12:00 AM server time.
     * It deletes all practice session records older than today.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void enforcePracticeRecordsRetentionPolicy() {
        // Data retention period
        LocalDate today = LocalDate.now();

        logger.info("Starting data retention task. Deleting practice sessions older than: " + today);

        try {
             practiceSessionService.deleteRecordsOlderThan(today);
            logger.info("Data retention task finished. Deleted yesterday practice session records.");
        } catch (Exception e) {
            logger.error("Error during practice sessions retention task." + e);
        }
    }

    /**
     * This task runs every day at 12:00 AM server time.
     * It deletes all chat messages older than today.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void enforceChatMessagesRetentionPolicy() {
        // Data retention period
        LocalDate today = LocalDate.now();

        logger.info("Starting data retention task. Deleting chat messages older than: " + today);

        try {
            chatService.deleteMessagesOlderThan(today);
            logger.info("Data retention task finished. Deleted old chat messages.");
        } catch (Exception e) {
            logger.error("Error during chat messages retention task." + e);
        }
    }

    /**
     * This task runs every day at 12:30 AM server time.
     * It deletes all notifications older than 30 days.
     * Daily is risky if users rely on notifications for important info and have not marked them as read
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void enforceNotificationsRetentionPolicy() {
        // Data retention period
        LocalDate today = LocalDate.now().plusDays(30); // keep notifications for 30 days

        logger.info("Starting data retention task. Deleting notifications older than: " + today);

        try {
            notificationService.deleteNotificationsOlderThan(today);
            logger.info("Data retention task finished. Deleted yesterday notifications.");
        } catch (Exception e) {
            logger.error("Error during notifications retention task." + e);
        }
    }

    /**
     * This task runs every day at 12:30 AM server time.
     * It deletes all battle logs older than today.
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void enforceBattleRetentionPolicy() {
        // Data retention period
        LocalDate today = LocalDate.now();

        logger.info("Starting data retention task. Deleting battle logs older than: " + today);

        try {
            battleService.deleteBattleLogsOlderThan(today);
            logger.info("Data retention task finished. Deleted yesterday battles.");
        } catch (Exception e) {
            logger.error("Error during battle retention task." + e);
        }
    }
}
