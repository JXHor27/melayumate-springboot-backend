package com.example.demo.flashcard.repo;

import com.example.demo.flashcard.entity.FlashcardList;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FlashcardListMapper {

    // --- Flashcard List Operations ---

    @Select("SELECT * FROM flashcard_list WHERE user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "flashcardListId", column = "flashcard_list_id"),
            @Result(property = "flashcardNumber", column = "flashcard_number")
    })
    List<FlashcardList> getFlashcardListsByUserId(String userId);

    @Select("SELECT * FROM flashcard_list WHERE flashcard_list_id = #{flashcardListId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "flashcardListId", column = "flashcard_list_id"),
            @Result(property = "flashcardNumber", column = "flashcard_number")
    })
    FlashcardList getFlashcardListByListId(String flashcardListId);

    @Insert("INSERT INTO flashcard_list(flashcard_list_id, user_id, title, description, flashcard_number) VALUES(#{flashcardListId}, #{userId}, #{title}, #{description}, #{flashcardNumber})")
    void addFlashcardList(FlashcardList flashcardList);

    @Update("UPDATE flashcard_list SET title = #{title}, description = #{description} WHERE flashcard_list_id = #{flashcardListId}")
    void editFlashcardList(FlashcardList flashcardList);

    @Update("UPDATE flashcard_list SET flashcard_number = flashcard_number + #{number}  WHERE flashcard_list_id = #{flashcardListId}")
    void updateFlashcardNumber(@Param("flashcardListId") String flashcardListId, @Param("number") int number);

    @Delete("DELETE FROM flashcard_list WHERE flashcard_list_id = #{flashcardListId}")
    void deleteFlashcardListById(String flashcardId);
}
