package com.example.demo.modules.lesson.repo;

import com.example.demo.modules.lesson.entity.Lesson;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LessonMapper {

    @Insert("INSERT INTO lessons(lesson_id, title, description, is_available, created_at) VALUES(#{lessonId}, #{title}, #{description}, #{isAvailable}, #{createdAt})")
    void createLesson(Lesson lesson);

    @Select("SELECT * FROM lessons ORDER BY created_at DESC")
    @Results({
            @Result(property = "lessonId", column = "lesson_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "isAvailable", column = "is_available")
    })
    List<Lesson> getAllLessons();

    @Select("SELECT * FROM lessons WHERE is_available = TRUE ORDER BY created_at DESC")
    @Results({
            @Result(property = "lessonId", column = "lesson_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "isAvailable", column = "is_available")
    })
    List<Lesson> getAvailableLessons();

    @Delete("DELETE FROM lessons WHERE lesson_id = #{lessonId}")
    void deleteLesson(String lessonId);

    @Update("UPDATE lessons SET is_available = #{isAvailable} WHERE lesson_id = #{lessonId}")
    void updateLessonAvailability(String lessonId, boolean isAvailable);


    @Select("SELECT * FROM lessons WHERE lesson_id = #{lessonId}")
    @Results({
            @Result(property = "lessonId", column = "lesson_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "isAvailable", column = "is_available")
    })
    Lesson getLessonById(String lessonId);
}
