package com.example.demo.dashboard.service;

import com.example.demo.auth.entity.UserEntity;
import com.example.demo.auth.repo.UserMapper;
import com.example.demo.dashboard.dto.PracticeCreateDTO;
import com.example.demo.dashboard.entity.PracticeCount;
import com.example.demo.dashboard.entity.PracticeSession;
import com.example.demo.dashboard.repo.PracticeSessionMapper;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.id.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    private final IdGenerator idGenerator;

    @Autowired
    private final StatsService statsService;

    public PracticeCount retrieveDailyStat(String userId){
        logger.info("Retrieving daily stat for user: " + userId);

        // If the user does not exist, throw an exception
        UserEntity user = userMapper.getUserByUserId(userId);
        if (user == null) {
            logger.error("User not found with id: " + userId);
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        PracticeCount practiceCount = practiceSessionMapper.getDailyStatByUserId(userId);
        logger.info("Retrieved daily stat: " + practiceCount);
        return practiceCount;
    }

    public PracticeSession recordPractice(PracticeCreateDTO dto) {
        logger.info("Recording practice session: " + dto);
        String userId = dto.getUserId();

        // If the user does not exist, throw an exception
        UserEntity user = userMapper.getUserByUserId(userId);
        if (user == null) {
            logger.error("User not found with id: " + userId);
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        PracticeSession session = new PracticeSession();
        session.setPracticeId(idGenerator.generatePracticeId());
        session.setUserId(dto.getUserId());
        session.setPracticeType(dto.getPracticeType());
        session.setCompletedAt(Instant.now());

        practiceSessionMapper.savePracticeSession(session);

        // After done one practice, asynchronously check if daily goal completed
        // send notification if completed, else do nothing
        checkDailyGoalComplete(userId);

        logger.info("Recorded practice session: " + session);
        return session;
    }

    public void checkDailyGoalComplete(String userId){
        PracticeCount practiceCount = retrieveDailyStat(userId);
        long practicesDoneToday = practiceCount.getFlashcardDone() + practiceCount.getDialogueDone() + practiceCount.getLessonDone();
        statsService.completeDailyGoal(userId, practicesDoneToday);
    }

    @Async
    public void deleteRecordsOlderThan(LocalDate date){
        logger.info("Deleting practice records older than: " + date);
        practiceSessionMapper.deleteRecordsOlderThan(date);
        logger.info("Finished deleting practice records older than: " + date);

    }


}
