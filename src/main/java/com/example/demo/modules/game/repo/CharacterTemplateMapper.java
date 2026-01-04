package com.example.demo.modules.game.repo;

import com.example.demo.modules.game.entity.CharacterTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CharacterTemplateMapper {
    @Select("SELECT * FROM character_templates ORDER BY unlock_level ASC, template_id ASC")
    @Results({
            @Result(property = "templateId", column = "template_id"),
            @Result(property = "characterName", column = "character_name"),
            @Result(property = "characterType", column = "character_type"),
            @Result(property = "imageUrl", column = "image_url"),
            @Result(property = "unlockLevel", column = "unlock_level"),
            @Result(property = "baseHp", column = "base_hp"),
            @Result(property = "baseAttack", column = "base_attack"),
            @Result(property = "baseDefense", column = "base_defense"),
            @Result(property = "baseSpeed", column = "base_speed"),
    })
    List<CharacterTemplate> findAllCharacterTemplates();

    @Select("SELECT * FROM character_templates WHERE template_id = #{templateId}")
    @Results({
            @Result(property = "templateId", column = "template_id"),
            @Result(property = "characterName", column = "character_name"),
            @Result(property = "characterType", column = "character_type"),
            @Result(property = "imageUrl", column = "image_url"),
            @Result(property = "unlockLevel", column = "unlock_level"),
            @Result(property = "baseHp", column = "base_hp"),
            @Result(property = "baseAttack", column = "base_attack"),
            @Result(property = "baseDefense", column = "base_defense"),
            @Result(property = "baseSpeed", column = "base_speed"),
    })
    CharacterTemplate findById(String templateId);
}