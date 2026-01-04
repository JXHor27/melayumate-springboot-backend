package com.example.demo.modules.game.repo;

import com.example.demo.modules.game.entity.Battle;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;

@Mapper
public interface BattleMapper {
    @Select("SELECT * FROM battles WHERE battle_id = #{battleId}")
    @Results({
            @Result(property = "battleId", column = "battle_id"),
            @Result(property = "challengerId", column = "challenger_id"),
            @Result(property = "defenderId", column = "defender_id"),
            @Result(property = "winnerId", column = "winner_id"),
            @Result(property = "battleLog", column = "battle_log"),
            @Result(property = "createdAt", column = "created_at")
    })
    Battle findByBattleId(String battleId);

    @Insert("INSERT INTO battles (battle_id, challenger_id, defender_id, winner_id, battle_log, created_at) VALUES (#{battleId}, #{challengerId}, #{defenderId}, #{winnerId}, #{battleLog}, #{createdAt})")
    void insert(Battle battle);

    @Delete("DELETE FROM battles WHERE DATE(created_at) < #{date}")
    void deleteBattlesOlderThan(LocalDate date);
}