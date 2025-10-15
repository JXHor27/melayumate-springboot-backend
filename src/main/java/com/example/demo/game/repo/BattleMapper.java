package com.example.demo.game.repo;

import com.example.demo.game.entity.Battle;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface BattleMapper {
    @Select("SELECT * FROM battle WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "battleLog", column = "battle_log"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "challenger", column = "challenger_id",
                    one = @One(select = "com.yourpackage.mappers.CharacterMapper.findById", fetchType = FetchType.EAGER)),
            @Result(property = "defender", column = "defender_id",
                    one = @One(select = "com.yourpackage.mappers.CharacterMapper.findById", fetchType = FetchType.EAGER)),
            @Result(property = "winner", column = "winner_id",
                    one = @One(select = "com.yourpackage.mappers.CharacterMapper.findById", fetchType = FetchType.EAGER))
    })
    Battle findById(Long id);

    // The @Options annotation is crucial here. It's the equivalent of useGeneratedKeys="true" in XML.
    // It tells MyBatis to retrieve the auto-generated ID from the database and set it on the 'battle' object's 'id' property.
    @Insert("INSERT INTO battle (challenger_id, defender_id, winner_id, battle_log, created_at) " +
            "VALUES (#{challenger.id}, #{defender.id}, #{winner.id}, #{battleLog}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    // The insert method will modify the battle object to add the generated ID
    void insert(Battle battle);
}