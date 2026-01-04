package com.example.demo.modules.lesson.repo;

import com.example.demo.modules.lesson.entity.Lesson;
import com.example.demo.modules.lesson.entity.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Insert("INSERT INTO questions(question_id, lesson_id, question_type, prompt_text, attributes, created_at) VALUES(#{questionId}, #{lessonId}, #{questionType}, #{promptText}, #{attributes}, #{createdAt})")
    void createQuestion(Question question);

    @Select("SELECT * FROM questions WHERE lesson_id = #{lessonId} ORDER BY question_id")
    @Results({
            @Result(property = "questionId", column = "question_id"),
            @Result(property = "lessonId", column = "lesson_id"),
            @Result(property = "questionType", column = "question_type"),
            @Result(property = "promptText", column = "prompt_text"),
            @Result(property = "attributes", column = "attributes"),
            @Result(property = "createdAt", column = "created_at"),
    })
    List<Question> getQuestionsByLessonId(String lessonId);

    @Delete("DELETE FROM questions WHERE question_id = #{questionId}")
    void deleteQuestion(String questionId);

    @Select("SELECT COUNT(*) FROM questions WHERE lesson_id = #{lessonId} ORDER BY question_id")
    int countByLessonId(String lessonId);

    @Select("SELECT * FROM questions WHERE question_id = #{questionId}")
    @Results({
            @Result(property = "questionId", column = "question_id"),
            @Result(property = "lessonId", column = "lesson_id"),
            @Result(property = "questionType", column = "question_type"),
            @Result(property = "promptText", column = "prompt_text"),
            @Result(property = "attributes", column = "attributes"),
            @Result(property = "createdAt", column = "created_at"),
    })
    Question findByQuestionId(String questionId);
}
