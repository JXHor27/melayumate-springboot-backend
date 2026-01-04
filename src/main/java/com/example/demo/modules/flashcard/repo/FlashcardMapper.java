package com.example.demo.modules.flashcard.repo;

import com.example.demo.modules.flashcard.entity.Flashcard;
import org.apache.ibatis.annotations.*;
import java.util.List;
@Mapper
public interface FlashcardMapper {

    @Select("SELECT * FROM flashcard WHERE flashcard_id = #{flashcardId}")
    @Results({
            @Result(property = "flashcardId", column = "flashcard_id"),
            @Result(property = "flashcardListId", column = "flashcard_list_id"),
            @Result(property = "englishWord", column = "english_word"),
            @Result(property = "malayWord", column = "malay_word")
    })
    Flashcard getFlashcardById(String flashcardId);

    @Select("SELECT * FROM flashcard WHERE flashcard_list_id = #{flashcardListId} ORDER BY flashcard_id")
    @Results({
            @Result(property = "flashcardId", column = "flashcard_id"),
            @Result(property = "flashcardListId", column = "flashcard_list_id"),
            @Result(property = "englishWord", column = "english_word"),
            @Result(property = "malayWord", column = "malay_word")
    })
    List<Flashcard> getFlashcardsByListId(String flashcardListId);

    @Insert("INSERT INTO flashcard(flashcard_id, flashcard_list_id, english_word, malay_word) VALUES(#{flashcardId}, #{flashcardListId}, #{englishWord}, #{malayWord})")
    void insertFlashcard(Flashcard flashcard);

    @Update("UPDATE flashcard SET english_word = #{englishWord}, malay_word = #{malayWord} WHERE flashcard_id = #{flashcardId}")
    void editFlashcard(Flashcard flashcard);

    @Delete("DELETE FROM flashcard WHERE flashcard_id = #{flashcardId}")
    void deleteFlashcardById(String flashcardId);
}
