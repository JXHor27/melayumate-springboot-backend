package com.example.demo.dashboard.service;

import com.example.demo.dashboard.dto.GoalCreateDTO;
import com.example.demo.dashboard.dto.NotificationCreateDTO;
import com.example.demo.dashboard.entity.PracticeCount;
import com.example.demo.dashboard.entity.UserStats;
import com.example.demo.dashboard.repo.UserStatsMapper;
import com.example.demo.enums.NotificationType;
import com.example.demo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StatsService {

    private static final Log logger = LogFactory.getLog(StatsService.class);

    @Autowired
    private final UserStatsMapper userStatsMapper;

    @Autowired
    private final NotificationService notificationService;

    public UserStats getUserStats(String userId){
        logger.info("Fetching stats for user: " + userId);

        // If the user stats does not exist, throw an exception
        UserStats userStats = userStatsMapper.getUserStatsByUserId(userId);
        if (userStats == null) {
            logger.error("User not found with id: " + userId);
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        logger.info("Fetched stats:" + userStats);
        return userStats;
    }

    public List<UserStats> findAllWithDailyGoal(){
        logger.info("Fetching user stats with daily goal.");

        List<UserStats> userStatsList = userStatsMapper.findAllWithDailyGoal();

        logger.info("Fetched user stats with daily goal:" + userStatsList);
        return userStatsList;
    }

    public void createInitialStats(String userId) {
        logger.info("Initializing stats for user: " + userId);

        UserStats userStats = new UserStats();
        userStats.setUserId(userId);
        userStats.setDailyGoal(0);
        userStats.setCurrentLevel(0);
        userStats.setCurrentExp(0);

        userStatsMapper.createInitialStats(userStats);

        logger.info("Initialized stats: " + userStats);
    }

    public UserStats updateGoal(GoalCreateDTO dto) {
        logger.info("Creating goal: " + dto);
        String userId = dto.getUserId();

        // If the user stats does not exist, throw an exception
        UserStats userStats = getUserStats(userId);

        userStats.setDailyGoal(dto.getDailyGoal());

        userStatsMapper.updateGoal(userStats);

        logger.info("Created daily goal: " + userStats);
        return userStats;
    }

    @Async
    public void completeDailyGoal(String userId, long practicesDoneToday){
        logger.info("Checking if daily goal is completed for user: " + userId);
        UserStats userStats = getUserStats(userId);

        if (practicesDoneToday == userStats.getDailyGoal()) {
            // Daily goal completed, create and send a notification.
            NotificationCreateDTO notification = new NotificationCreateDTO();
            notification.setUserId(userId);
            notification.setNotificationType(NotificationType.ACHIEVEMENT);
            notification.setTitle("Daily Goal Completed");
            notification.setMessage("Congratulations, you have completed daily goal!");
            notificationService.createNotification(notification);
        }

        logger.info("Daily goal checked completed for user: " + userId);
    }
}
