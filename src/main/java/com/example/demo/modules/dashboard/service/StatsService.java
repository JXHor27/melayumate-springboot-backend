package com.example.demo.modules.dashboard.service;

import com.example.demo.modules.dashboard.dto.GoalCreateDTO;
import com.example.demo.modules.dashboard.dto.NotificationCreateDTO;
import com.example.demo.modules.dashboard.entity.UserStats;
import com.example.demo.modules.dashboard.repo.UserStatsMapper;
import com.example.demo.enums.NotificationType;
import com.example.demo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    /**
     * Retrieves the stats for a specific user by their user ID.
     * The result is cached under "userGameState" cache.
     *
     * @param userId The ID of the user.
     * @return The {@link UserStats} object for the specified user.
     * @throws ResourceNotFoundException if the user stats do not exist.
     */
    @Cacheable(value = "userGameState", key = "#userId")
    public UserStats getUserStats(String userId){
        logger.info("Fetching stats for user: " + userId);
        UserStats userStats = userStatsMapper.getUserStatsByUserId(userId);
        if (userStats == null) {
            logger.error("User stats not found with id: " + userId);
            throw new ResourceNotFoundException("User stats not found with id: " + userId);
        }
        logger.info("Fetched stats:" + userStats);
        return userStats;
    }

    /**
     * Updates the daily goal for a specific user.
     * Also updates the cache for the user's game state.
     *
     * @param goalCreateDTO The {@link GoalCreateDTO} containing the user ID and new daily goal.
     * @return The updated {@link UserStats} object.
     */
    @CachePut(value = "userGameState", key = "#goalCreateDTO.userId")
    public UserStats updateGoal(GoalCreateDTO goalCreateDTO) {
        logger.info("Creating goal: " + goalCreateDTO);
        String userId = goalCreateDTO.getUserId();
        UserStats userStats = userStatsMapper.getUserStatsByUserId(userId);
        userStats.setDailyGoal(goalCreateDTO.getDailyGoal());
        userStatsMapper.updateDailyGoal(userStats);
        logger.info("Created daily goal: " + userStats);
        return userStats;
    }

    /**
     * Adds experience points to a user's stats and handles level-up logic.
     * Also updates the cache for the user's game state.
     *
     * @param userId The ID of the user.
     * @param amount The amount of experience points to add.
     * @return The updated {@link UserStats} object.
     */
    @CachePut(value = "userGameState", key = "#userId")
    public UserStats addExperience(String userId, int amount) {
        logger.info("Adding " + amount + " EXP to user: " + userId);
        UserStats userStats = userStatsMapper.getUserStatsByUserId(userId);
        userStats.setCurrentExp(userStats.getCurrentExp() + amount);

        // Level 1 = 100 exp, Level 2 = 200 exp, Level 3 = 300 exp, and so on
        int expForNextLevel = (userStats.getCurrentLevel() + 1) * 100;
        if (userStats.getCurrentExp() >= expForNextLevel) {
            userStats.setCurrentLevel(userStats.getCurrentLevel() + 1);

            NotificationCreateDTO notification = new NotificationCreateDTO();
            notification.setUserId(userId);
            notification.setNotificationType(NotificationType.ACHIEVEMENT);
            notification.setTitle("Level Up!");
            notification.setMessage("Congratulations, you have reached Level " + userStats.getCurrentLevel() + "!");
            notificationService.createNotification(notification);
            // Optionally reset exp or carry it over
            // seems we need to reset exp for bar display
            // userStats.setCurrentExp(userStats.getCurrentExp() - expForNextLevel);
        }
        userStatsMapper.updateUserStats(userStats);
        logger.info("Done adding EXP: " + userStats);
        return userStats;
    }

    /**
     * Creates initial stats for a new user.
     *
     * @param userId   The ID of the user.
     * @param username The username of the user.
     */
    public void createInitialStats(String userId, String username) {
        logger.info("Initializing stats for user: " + userId);
        UserStats userStats = new UserStats();
        userStats.setUserId(userId);
        userStats.setUsername(username);
        userStats.setDailyGoal(0);
        userStats.setCurrentLevel(0);
        userStats.setCurrentExp(0);
        userStatsMapper.createInitialStats(userStats);
        logger.info("Initialized stats: " + userStats);
    }

    /**
     * Retrieves all user stats that have a daily goal set with at least 1.
     *
     * @return A list of {@link UserStats} objects with daily goals.
     */
    public List<UserStats> findAllWithDailyGoal(){
        logger.info("Fetching user stats with daily goal.");
        List<UserStats> userStatsList = userStatsMapper.findAllWithDailyGoal();
        logger.info("Fetched user stats with daily goal:" + userStatsList);
        return userStatsList;
    }

//    /**
//     * Asynchronously checks if the user has completed their daily goal and sends a notification if so.
//     *
//     * @param userId             The ID of the user.
//     * @param practicesDoneToday The number of practices the user has completed today.
//     */
//    @Async
//    public void completeDailyGoal(String userId, long practicesDoneToday){
//        logger.info("Checking if daily goal is completed for user: " + userId);
//        UserStats userStats = userStatsMapper.getUserStatsByUserId(userId);
//        if (practicesDoneToday == userStats.getDailyGoal()) {
//            NotificationCreateDTO notification = new NotificationCreateDTO();
//            notification.setUserId(userId);
//            notification.setNotificationType(NotificationType.ACHIEVEMENT);
//            notification.setTitle("Daily Goal Completed");
//            notification.setMessage("Congratulations, you have completed daily goal!");
//            notificationService.createNotification(notification);
//        }
//        logger.info("Daily goal checked completed for user: " + userId);
//    }

}
