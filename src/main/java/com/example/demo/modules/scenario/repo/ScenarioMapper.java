package com.example.demo.modules.scenario.repo;

import com.example.demo.modules.scenario.entity.Scenario;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScenarioMapper {

    @Select("SELECT * FROM scenario WHERE user_id = #{userId} ORDER BY scenario_id")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "scenarioId", column = "scenario_id"),
            @Result(property = "dialogueNumber", column = "dialogue_number")
    })
    List<Scenario> getScenariosByUserId(String userId);

    @Select("SELECT * FROM scenario WHERE scenario_id = #{scenarioId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "scenarioId", column = "scenario_id"),
            @Result(property = "dialogueNumber", column = "dialogue_number")
    })
    Scenario getScenarioById(String scenarioListId);

    @Insert("INSERT INTO scenario(scenario_id, user_id, title, description, dialogue_number) VALUES(#{scenarioId}, #{userId}, #{title}, #{description}, #{dialogueNumber})")
    void addScenario(Scenario scenario);

    @Update("UPDATE scenario SET title = #{title}, description = #{description} WHERE scenario_id = #{scenarioId}")
    void editScenario(Scenario scenario);

    @Delete("DELETE FROM scenario WHERE scenario_id = #{scenarioId}")
    void deleteScenario(String scenarioId);

    @Update("UPDATE scenario SET dialogue_number = dialogue_number + #{number}  WHERE scenario_id = #{scenarioId}")
    void updateDialogueNumber(String scenarioId, int number);
}
