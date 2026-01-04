package com.example.demo.modules.lesson.repo;

import com.example.demo.modules.lesson.dto.result.AnswerDistribution;
import com.example.demo.modules.lesson.entity.Answer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnswerMapper {

    @Insert("INSERT INTO answers(answer_id, user_id, question_id, lesson_id, selected_answer, is_correct, answered_at) VALUES(#{answerId}, #{userId}, #{questionId}, #{lessonId}, #{selectedAnswer}, #{isCorrect}, #{answeredAt})")
    void answerQuestion(Answer answer);

    @Select("SELECT COUNT(*) FROM answers WHERE user_id = #{userId} AND lesson_id = #{lessonId}")
    int countCompletedQuestionsForUserInLesson(String userId, String lessonId);

    @Select("SELECT * FROM answers WHERE user_id = #{userId} AND lesson_id = #{lessonId} ORDER BY answered_at")
    @Results({
            @Result(property = "answerId", column = "answer_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "questionId", column = "question_id"),
            @Result(property = "lessonId", column = "lesson_id"),
            @Result(property = "selectedAnswer", column = "selected_answer"),
            @Result(property = "isCorrect", column = "is_correct"),
            @Result(property = "answeredAt", column = "answered_at")
    })
    List<Answer> findMostRecentAnswersForUserInLesson(String userId, String lessonId);

    /**
     * Gets the total number of attempts for a given question.
     */
    @Select("SELECT COUNT(*) FROM answers WHERE question_id = #{questionId}")
    int countTotalAttemptsByQuestionId(String questionId);

    /**
     * Gets the number of correct attempts for a given question.
     */
    @Select("SELECT COUNT(*) FROM answers WHERE question_id = #{questionId} AND is_correct = TRUE")
    int countCorrectAttemptsByQuestionId(String questionId);

    /**
     * Gets the distribution of selected answers for a given question.
     * Returns a list of objects, where each object contains the answer and its count.
     */
    @Select("SELECT selected_answer AS answer_option, COUNT(*) AS count " +
            "FROM answers " +
            "WHERE question_id = #{questionId} " +
            "GROUP BY selected_answer ORDER BY count DESC")
    @Results({
            @Result(property = "answerOption", column = "answer_option"),
    })
    List<AnswerDistribution> getAnswerDistributionByQuestionId(String questionId);
}
