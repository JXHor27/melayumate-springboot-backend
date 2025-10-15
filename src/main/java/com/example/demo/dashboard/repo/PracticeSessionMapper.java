package com.example.demo.dashboard.repo;
import com.example.demo.dashboard.entity.PracticeCount;
import com.example.demo.dashboard.entity.PracticeSession;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;

@Mapper
public interface PracticeSessionMapper {
    @Insert("INSERT INTO practice_sessions(practice_id, user_id, practice_type, completed_at) VALUES(#{practiceId}, #{userId}, #{practiceType}, #{completedAt})")
    void savePracticeSession(PracticeSession practiceSession);

    @Select("SELECT " +
            "COUNT(CASE WHEN practice_type = 'FLASHCARD' THEN 1 END) AS flashcard_done, " +
            "COUNT(CASE WHEN practice_type = 'LESSON' THEN 1 END) AS lesson_done, " +
            "COUNT(CASE WHEN practice_type = 'DIALOGUE' THEN 1 END) AS dialogue_done " +
            "FROM " +
            "practice_sessions " +
            "WHERE " +
            "user_id = #{userId}" +
            "AND DATE(completed_at) = CURDATE()")
    @Results({
            @Result(property = "flashcardDone", column = "flashcard_done"),
            @Result(property = "dialogueDone", column = "dialogue_done"),
            @Result(property = "lessonDone", column = "lesson_done")
    })
    PracticeCount getDailyStatByUserId(String userId);

    @Delete("DELETE FROM practice_sessions WHERE DATE(completed_at) < #{date}")
    void deleteRecordsOlderThan(LocalDate date);
}
