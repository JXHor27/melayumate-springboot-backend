package com.example.demo.modules.dashboard.service;

import com.example.demo.auth.entity.UserEntity;
import com.example.demo.auth.repo.UserMapper;
import com.example.demo.constant.GameConstant;
import com.example.demo.enums.NotificationType;
import com.example.demo.enums.PracticeType;
import com.example.demo.modules.dashboard.dto.NotificationCreateDTO;
import com.example.demo.modules.dashboard.dto.PracticeCreateDTO;
import com.example.demo.modules.dashboard.entity.PracticeCount;
import com.example.demo.modules.dashboard.entity.PracticeSession;
import com.example.demo.modules.dashboard.entity.UserStats;
import com.example.demo.modules.dashboard.repo.PracticeSessionMapper;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.modules.flashcard.dto.FlashcardListCreateDTO;
import com.example.demo.modules.flashcard.service.FlashcardListService;
import com.example.demo.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class PracticeSessionService {

    private static final Log logger = LogFactory.getLog(PracticeSessionService.class);

    @Autowired
    private final PracticeSessionMapper practiceSessionMapper;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final StatsService statsService;

    @Autowired
    private final FlashcardListService flashcardListService;

    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final IdGeneratorService idGeneratorService;

    /**
     * Retrieve the daily practice statistics for a user.
     *
     * @param userId The ID of the user.
     * @return The {@link PracticeCount} object containing daily statistics.
     * @throws ResourceNotFoundException if the user does not exist.
     */
    public PracticeCount retrieveDailyStat(String userId){
        logger.info("Retrieving daily stat for user: " + userId);

        UserEntity user = userMapper.getUserByUserId(userId);
        if (user == null) {
            logger.error("User not found with id: " + userId);
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        PracticeCount practiceCount = practiceSessionMapper.getDailyStatByUserId(userId);
        logger.info("Retrieved daily stat: " + practiceCount);
        return practiceCount;
    }

    /**
     * Record a new practice session for a user.
     *
     * @param dto The {@link PracticeCreateDTO} containing practice session details.
     * @return The recorded {@link PracticeSession} object.
     * @throws ResourceNotFoundException if the user does not exist.
     */
    @Transactional
    public PracticeSession recordPractice(PracticeCreateDTO dto) {
        logger.info("Recording practice session: " + dto);
        String userId = dto.getUserId();

        UserEntity user = userMapper.getUserByUserId(userId);
        if (user == null) {
            logger.error("User not found with id: " + userId);
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        PracticeSession session = new PracticeSession();
        session.setPracticeId(idGeneratorService.generatePracticeId());
        session.setUserId(dto.getUserId());
        session.setPracticeType(dto.getPracticeType());
        session.setCompletedAt(Instant.now());

        practiceSessionMapper.savePracticeSession(session);
        UserStats userStats = statsService.addExperience(userId, GameConstant.PRACTICE_EXP);

        checkIfDailyGoalCompleted(userStats);

        if (dto.getPracticeType() == PracticeType.FLASHCARD) {
            flashcardListService.scheduleNextReviewDate(dto.getLearningSessionId(), dto.getCorrectCount(), dto.getTotalCount());
        }

        logger.info("Recorded practice session: " + session);
        return session;
    }

    /**
     * Check if the user has completed their daily goal and send a notification if so.
     *
     * @param userStats The {@link UserStats} object of the user.
     */
    public void checkIfDailyGoalCompleted(UserStats userStats){
        String userId = userStats.getUserId();
        logger.info("Checking if daily goal is completed for user: " + userId);
        PracticeCount practiceCount = retrieveDailyStat(userId);
        long practicesDoneToday = practiceCount.getFlashcardDone() + practiceCount.getDialogueDone() + practiceCount.getLessonDone();
        if (practicesDoneToday == userStats.getDailyGoal()) {
            NotificationCreateDTO notification = new NotificationCreateDTO();
            notification.setUserId(userId);
            notification.setNotificationType(NotificationType.ACHIEVEMENT);
            notification.setTitle("Daily Goal Completed");
            notification.setMessage("Congratulations, you have completed daily goal!");
            notificationService.createNotification(notification);
        }
        logger.info("Daily goal checked completed for user: " + userId);
    }

    /**
     * Asynchronously delete practice records older than the specified date.
     *
     * @param date The cutoff date; records older than this date will be deleted.
     */
    @Async
    public void deleteRecordsOlderThan(LocalDate date){
        logger.info("Deleting practice records older than: " + date);
        practiceSessionMapper.deleteRecordsOlderThan(date);
        logger.info("Finished deleting practice records older than: " + date);

    }
}
