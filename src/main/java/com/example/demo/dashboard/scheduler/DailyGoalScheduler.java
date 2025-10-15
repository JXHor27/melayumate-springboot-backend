package com.example.demo.dashboard.scheduler;

import com.example.demo.dashboard.dto.NotificationCreateDTO;
import com.example.demo.dashboard.entity.PracticeCount;
import com.example.demo.dashboard.entity.UserStats;
import com.example.demo.dashboard.service.NotificationService;
import com.example.demo.dashboard.service.PracticeSessionService;
import com.example.demo.dashboard.service.StatsService;
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
     * This task runs every day at 10:00 AM server time.
     * It finds all users who have not met their daily goal for TODAY and sends a notification.
     */
    @Scheduled(cron = "0 0 10 * * ?") // Cron expression for 10:00:00 AM daily
    public void checkDailyGoalsAndNotify() {
        logger.info("Starting daily goal check job...");

        // Get all users who have a daily goal set
        List<UserStats> userStatsList = statsService.findAllWithDailyGoal();

        // For each user, check their progress for the current day
         for (UserStats userStats : userStatsList) {
             int goal = userStats.getDailyGoal();
             PracticeCount practiceCount = practiceSessionService.retrieveDailyStat(userStats.getUserId());

             long practicesDoneToday = practiceCount.getFlashcardDone() + practiceCount.getDialogueDone() + practiceCount.getLessonDone();

             if (practicesDoneToday < goal) {
                 // If the goal is not met, create and send a notification.
                 NotificationCreateDTO notification = new NotificationCreateDTO();
                 notification.setUserId(userStats.getUserId());
                 notification.setNotificationType(NotificationType.GOAL_REMINDER);
                 notification.setTitle("Daily Goal Reminder");
                 notification.setMessage("You're close to your daily goal! Keep up the great work.");
                 notificationService.createNotification(notification);
                 logger.info("Sent daily goal reminder to user: " + userStats.getUserId());
             }
         }

        logger.info("Daily goal check job finished.");
    }
}
