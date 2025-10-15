package com.example.demo.user;
import org.apache.ibatis.annotations.*;
import com.example.demo.auth.entity.UserEntity;

@Mapper
public interface UserStatMapper {
    @Insert("INSERT INTO user_today_stat(user_id, flashcard_done, lesson_done, scenario_done) " +
            "VALUES(#{userId}, #{flashcardDone}, #{lessonDone}, #{scenarioDone}) " +
            "ON DUPLICATE KEY UPDATE " +
            "  flashcard_done = flashcard_done + 1")
    void upsertUserStatFlashcard(UserEntity user);

    @Insert("INSERT INTO user_today_stat(user_id, flashcard_done, lesson_done, scenario_done) " +
            "VALUES(#{userId}, #{flashcardDone}, #{lessonDone}, #{scenarioDone}) " +
            "ON DUPLICATE KEY UPDATE " +
            "  scenario_done = scenario_done + 1")   // or #{stat.scenarioDone}
    void upsertUserStatScenario(UserEntity user);

    @Insert("INSERT INTO user_today_stat(user_id, current_exp) " +
            "VALUES(#{userId}, #{currentEXP}) " +
            "ON DUPLICATE KEY UPDATE " +
            "  current_exp = current_exp + VALUES(current_exp) ") // or #{stat.flashcardDone}
    void upsertUserExp(UserEntity user);

    @Select("SELECT * FROM user_today_stat WHERE user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "flashcardDone", column = "flashcard_done"),
            @Result(property = "lessonDone", column = "lesson_done"),
            @Result(property = "scenarioDone", column = "scenario_done"),
            @Result(property = "currentLevel", column = "current_level"),
            @Result(property = "currentEXP", column = "current_exp")

    })
    User getUserStat(int userId);
}
