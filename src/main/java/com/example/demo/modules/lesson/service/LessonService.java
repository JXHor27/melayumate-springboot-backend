package com.example.demo.modules.lesson.service;

import com.example.demo.enums.NotificationType;
import com.example.demo.modules.dashboard.dto.NotificationCreateDTO;
import com.example.demo.modules.dashboard.service.NotificationService;
import com.example.demo.modules.lesson.dto.result.LessonProgressDTO;
import com.example.demo.modules.lesson.entity.Lesson;
import com.example.demo.modules.lesson.dto.request.LessonCreateDTO;
import com.example.demo.modules.lesson.repo.AnswerMapper;
import com.example.demo.modules.lesson.repo.LessonMapper;
import com.example.demo.modules.lesson.repo.QuestionMapper;
import com.example.demo.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private static final Log logger = LogFactory.getLog(LessonService.class);

    @Autowired
    private final LessonMapper lessonMapper;

    @Autowired
    private final AnswerMapper answerMapper;

    @Autowired
    private final QuestionMapper questionMapper;

    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final IdGeneratorService idGeneratorService;

    public Lesson createLesson(LessonCreateDTO lessonCreateDTO) {
        logger.info("Creating lesson: " + lessonCreateDTO);
        Lesson lesson = new Lesson();
        lesson.setLessonId(idGeneratorService.generateLessonId());
        lesson.setTitle(lessonCreateDTO.getTitle());
        lesson.setDescription(lessonCreateDTO.getDescription());
        lesson.setCreatedAt(Instant.now());
        lesson.setAvailable(false); // New lessons are unavailable by default
        lessonMapper.createLesson(lesson);
        logger.info("Created lesson: " + lesson);
        return lesson;
    }

    public Lesson getLessonById(String lessonId) {
        logger.info("Fetching lesson with id: " + lessonId);
        Lesson lesson = lessonMapper.getLessonById(lessonId);
        if (lesson == null) {
            logger.info("Lesson not found with id: " + lessonId);
            return null;
        }
        logger.info("Fetched lesson: " + lesson);
        return lesson;
    }

    public List<Lesson> getAllLessons() {
        logger.info("Fetching all lessons");
        List<Lesson> allLessons = lessonMapper.getAllLessons();
        if (allLessons.isEmpty()) {
            logger.info("No lessons found");
            return allLessons;
        }
        logger.info("Fetched lessons: " + allLessons);
        return allLessons;

    }

    public List<Lesson> getAvailableLessons() {
        logger.info("Fetching available lessons");
        List<Lesson> availableLessons = lessonMapper.getAvailableLessons();
        if (availableLessons.isEmpty()) {
            logger.info("No available lessons found");
            return availableLessons;
        }
        logger.info("Fetched available lessons: " + availableLessons);
        return availableLessons;
    }

    public void deleteLesson(String lessonId) {
        logger.info("Deleting lesson with id: " + lessonId);
        lessonMapper.deleteLesson(lessonId);
        logger.info("Deleted lesson with id: " + lessonId);
    }

    @Transactional
    public void updateLessonAvailability(String lessonId, boolean isAvailable) {
        logger.info("Updating lesson availability with id: " + lessonId);
        Lesson lesson = lessonMapper.getLessonById(lessonId);

        // --- THE TRIGGER LOGIC ---
        // Check if the visibility is changing from 'false' to 'true'
        if (!lesson.isAvailable() && isAvailable) {
            // Update the lesson in the database
            lessonMapper.updateLessonAvailability(lessonId, true);
            // --- TRIGGER THE ASYNC BROADCAST ---
            // This call returns instantly.
            notificationService.broadcastNewLessonNotification(lesson);

        } else {
            // Just update the visibility without broadcasting
            lessonMapper.updateLessonAvailability(lessonId, isAvailable);
        }
        logger.info("Updated lesson availability with id: " + lessonId);
    }

    public List<LessonProgressDTO> getAvailableLessonsWithProgress(String userId) {
        logger.info("Fetching available lessons");
        List<Lesson> availableLessons = lessonMapper.getAvailableLessons();

        List<LessonProgressDTO> dtoList = new ArrayList<>();

        if (availableLessons.isEmpty()) {
            logger.info("No available lessons found");
            return dtoList;
        }

        // Calculate the user's progress for each lesson
        for (Lesson lesson : availableLessons) {
            // Get total questions for this lesson
            int totalQuestions = questionMapper.countByLessonId(lesson.getLessonId());

            // Get how many of those questions the user has answered correctly
            int completedCount = answerMapper.countCompletedQuestionsForUserInLesson(userId, lesson.getLessonId());
            LessonProgressDTO dto = new LessonProgressDTO();
            dto.setLessonId(lesson.getLessonId());
            dto.setTitle(lesson.getTitle());
            dto.setDescription(lesson.getDescription());
            dto.setQuestionCount(totalQuestions);
            dto.setCompletedQuestions(completedCount);
            dtoList.add(dto);
        }
        logger.info("Fetched available lessons: " + dtoList);
        return dtoList;
    }


}
