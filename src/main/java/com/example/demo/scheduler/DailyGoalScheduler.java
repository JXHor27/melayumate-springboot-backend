package com.example.demo.scheduler;

import com.example.demo.modules.dashboard.dto.NotificationCreateDTO;
import com.example.demo.modules.dashboard.entity.PracticeCount;
import com.example.demo.modules.dashboard.entity.UserStats;
import com.example.demo.modules.dashboard.service.NotificationService;
import com.example.demo.modules.dashboard.service.PracticeSessionService;
import com.example.demo.modules.dashboard.service.StatsService;
import com.example.demo.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DailyGoalScheduler {

    private static final Log logger = LogFactory.getLog(DailyGoalScheduler.class);

    @Autowired
    private final StatsService statsService;

    @Autowired
    private final PracticeSessionService practiceSessionService;

    @Autowired
    private final NotificationService notificationService;

    /**
     * This task runs every day at 12:00 PM server time.
     * It finds all users who have not met their daily goal for TODAY and sends a notification.
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void checkDailyGoalsAndNotify() {
        logger.info("Starting daily goal check job...");

        // Get all users who have a daily goal set
        List<UserStats> userStatsList = statsService.findAllWithDailyGoal();

        // For each user, check their progress for the current day
         for (UserStats userStats : userStatsList) {
             int goal = userStats.getDailyGoal();
             String userId = userStats.getUserId();
             PracticeCount practiceCount = practiceSessionService.retrieveDailyStat(userId);

             long practicesDoneToday = practiceCount.getFlashcardDone() + practiceCount.getDialogueDone() + practiceCount.getLessonDone();

             if (practicesDoneToday < goal) {
                 // If the goal is not met, create and send a notification.
                 NotificationCreateDTO notification = new NotificationCreateDTO();
                 notification.setUserId(userId);
                 notification.setNotificationType(NotificationType.REMINDER);
                 notification.setTitle("Daily Goal Reminder");
                 notification.setMessage("You're close to your daily goal! Keep up the great work.");
                 notificationService.createNotification(notification);
                 logger.info("Sent daily goal reminder to user: " + userId);
             }
         }

        logger.info("Daily goal check job finished.");
    }
}
