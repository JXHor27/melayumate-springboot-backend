package com.example.demo.scenario.repo;

import com.example.demo.scenario.entity.Scenario;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScenarioMapper {

    @Select("SELECT * FROM scenario WHERE user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "scenarioId", column = "scenario_id")
    })
    List<Scenario> getScenariosByUserId(String userId);

    @Select("SELECT * FROM scenario WHERE scenario_id = #{scenarioId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "scenarioId", column = "scenario_id")
    })
    Scenario getScenarioById(String scenarioListId);

    @Insert("INSERT INTO scenario(scenario_id, user_id, title, description) VALUES(#{scenarioId}, #{userId}, #{title}, #{description})")
    void addScenario(Scenario scenario);

    @Update("UPDATE scenario SET title = #{title}, description = #{description} WHERE scenario_id = #{scenarioId}")
    void editScenario(Scenario scenario);

    @Delete("DELETE FROM scenario WHERE scenario_id = #{scenarioId}")
    void deleteScenario(String scenarioId);
}
