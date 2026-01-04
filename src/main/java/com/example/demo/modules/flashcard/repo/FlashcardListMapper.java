package com.example.demo.modules.flashcard.repo;

import com.example.demo.modules.flashcard.entity.FlashcardList;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FlashcardListMapper {

    @Select("SELECT * FROM flashcard_list WHERE user_id = #{userId} ORDER BY flashcard_list_id")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "flashcardListId", column = "flashcard_list_id"),
            @Result(property = "flashcardNumber", column = "flashcard_number"),
            @Result(property = "isRandom", column = "is_random"),
            @Result(property = "defaultLanguage", column = "default_language"),
            @Result(property = "nextReviewDate", column = "next_review_date"),
            @Result(property = "currentStreak", column = "current_streak")
    })
    List<FlashcardList> getFlashcardListsByUserId(String userId);

    @Select("SELECT * FROM flashcard_list WHERE flashcard_list_id = #{flashcardListId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "flashcardListId", column = "flashcard_list_id"),
            @Result(property = "flashcardNumber", column = "flashcard_number"),
            @Result(property = "isRandom", column = "is_random"),
            @Result(property = "defaultLanguage", column = "default_language"),
            @Result(property = "nextReviewDate", column = "next_review_date"),
            @Result(property = "currentStreak", column = "current_streak")
    })
    FlashcardList getFlashcardListByListId(String flashcardListId);

    @Insert("INSERT INTO flashcard_list(flashcard_list_id, user_id, title, description, flashcard_number, is_random, default_language, next_review_date, current_streak) VALUES(#{flashcardListId}, #{userId}, #{title}, #{description}, #{flashcardNumber}, #{isRandom}, #{defaultLanguage}, #{nextReviewDate}, #{currentStreak})")
    void addFlashcardList(FlashcardList flashcardList);

    @Update("UPDATE flashcard_list SET title = #{title}, description = #{description} WHERE flashcard_list_id = #{flashcardListId}")
    void editFlashcardList(FlashcardList flashcardList);

    @Update("UPDATE flashcard_list SET flashcard_number = flashcard_number + #{number}  WHERE flashcard_list_id = #{flashcardListId}")
    void updateFlashcardNumber(@Param("flashcardListId") String flashcardListId, @Param("number") int number);

    @Delete("DELETE FROM flashcard_list WHERE flashcard_list_id = #{flashcardListId}")
    void deleteFlashcardListById(String flashcardListId);

    @Update("UPDATE flashcard_list SET is_random = #{isRandom} WHERE flashcard_list_id = #{flashcardListId}")
    void updateOrder(String flashcardListId, boolean isRandom);

    @Update("UPDATE flashcard_list SET default_language = #{defaultLanguage} WHERE flashcard_list_id = #{flashcardListId}")
    void updateDefaultLanguage(String flashcardListId, String defaultLanguage);

    @Update("UPDATE flashcard_list SET next_review_date = #{nextReviewDate}, current_streak = #{currentStreak} WHERE flashcard_list_id = #{flashcardListId}")
    void updateFlashcardListReviewDate(FlashcardList flashcardList);

    @Select("SELECT * FROM flashcard_list ORDER BY flashcard_list_id")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "flashcardListId", column = "flashcard_list_id"),
            @Result(property = "flashcardNumber", column = "flashcard_number"),
            @Result(property = "isRandom", column = "is_random"),
            @Result(property = "defaultLanguage", column = "default_language"),
            @Result(property = "nextReviewDate", column = "next_review_date"),
            @Result(property = "currentStreak", column = "current_streak")
    })
    List<FlashcardList> getAllFlashcardLists();
}
