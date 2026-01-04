package com.example.demo.modules.scenario.repo;

import com.example.demo.modules.scenario.entity.Dialogue;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DialogueMapper {
    @Select("SELECT * " +
            "FROM dialogue " +
            "WHERE scenario_id = #{scenarioId} " +
            "ORDER BY " +
            "dialogue_order ASC, " +
            "CASE " +
            "WHEN dialogue_type = 'QUESTION' THEN 1 " +
            "WHEN dialogue_type = 'RESPONSE' THEN 2 " +
            "ELSE 3 " +
            "END")
    @Results({
            @Result(property = "dialogueId", column = "dialogue_id"),
            @Result(property = "scenarioId", column = "scenario_id"),
            @Result(property = "dialogueType", column = "dialogue_type"),
            @Result(property = "dialogueOrder", column = "dialogue_order"),
            @Result(property = "audioUrl", column = "audio_url")
    })
    List<Dialogue> getDialogueListById(String scenarioId);

    @Select("SELECT * FROM dialogue WHERE dialogue_id = #{dialogueId}")
    @Results({
            @Result(property = "dialogueId", column = "dialogue_id"),
            @Result(property = "scenarioId", column = "scenario_id"),
            @Result(property = "dialogueType", column = "dialogue_type"),
            @Result(property = "dialogueOrder", column = "dialogue_order"),
            @Result(property = "audioUrl", column = "audio_url")
    })
    Dialogue getDialogueById(String dialogueId);

    @Insert("INSERT INTO dialogue(dialogue_id, scenario_id, dialogue_type, english, malay, dialogue_order, audio_url) VALUES(#{dialogueId}, #{scenarioId}, #{dialogueType}, #{english}, #{malay}, #{dialogueOrder}, #{audioUrl})")
    void addDialogue(Dialogue dialogue);

    @Update("UPDATE INTO dialogue(dialogue_id, scenario_id, dialogue_type, english, malay, dialogue_order, audio_url) VALUES(#{dialogueId}, #{scenarioId}, #{dialogueType}, #{english}, #{malay}, #{dialogueOrder}, #{audioUrl})")
    void uploadAudioURL(String objectKey);

    @Delete("DELETE FROM dialogue WHERE dialogue_id = #{dialogueId}")
    void deleteDialogue(String dialogueId);
}
