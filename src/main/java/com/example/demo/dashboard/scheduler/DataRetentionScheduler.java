package com.example.demo.dashboard.scheduler;

import com.example.demo.dashboard.service.NotificationService;
import com.example.demo.dashboard.service.PracticeSessionService;
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

    /**
     * This task runs every day at 5:00 AM server time.
     * It deletes all practice session records older than today.
     */
    @Scheduled(cron = "0 0 22 * * ?")
    public void enforcePracticeRecordsRetentionPolicy() {
        // Define data retention period.
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
     * This task runs every day at 6:00 AM server time.
     * It deletes all notifications older than today.
     */
    @Scheduled(cron = "0 10 22 * * ?")
    public void enforceNotificationsRetentionPolicy() {
        // Define data retention period.
        LocalDate today = LocalDate.now();

        logger.info("Starting data retention task. Deleting notifications older than: " + today);

        try {
            notificationService.deleteNotificationsOlderThan(today);
            logger.info("Data retention task finished. Deleted yesterday notifications.");
        } catch (Exception e) {
            logger.error("Error during notifications retention task." + e);
        }
    }
}
