package com.example.demo.game.repo;

import com.example.demo.game.entity.GameCharacter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CharacterMapper {
    @Select("SELECT * FROM character WHERE id = #{id}")
    GameCharacter findById(String id);

    @Select("SELECT * FROM character WHERE status = 'LISTED_FOR_BATTLE' AND user_id != #{userId}")
    List<GameCharacter> findListedForBattle(Long userId);

    @Update("UPDATE character " +
            "SET " +
            "status = #{status}, " +
            "listed_at = #{listedAt} " +
            "WHERE id = #{id}")
    void update(GameCharacter character);
}