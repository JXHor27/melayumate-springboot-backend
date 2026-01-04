package com.example.demo.modules.game.repo;


import com.example.demo.modules.game.entity.CharacterInstance;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CharacterInstanceMapper {
    @Insert("INSERT INTO characters (character_id, user_id, character_template_id, unlocked_at, is_primary, character_status, listed_at) VALUES (#{characterId}, #{userId}, #{characterTemplateId}, #{unlockedAt}, #{isPrimary}, #{characterStatus}, #{listedAt})")
    void insert(CharacterInstance instance);

    @Select("SELECT COUNT(*) FROM characters WHERE user_id = #{userId}")
    int countByUserId(String userId);

    @Select("SELECT * FROM characters WHERE character_id = #{characterId}")
    @Results({
            @Result(property = "characterId", column = "character_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "characterTemplateId", column = "character_template_id"),
            @Result(property = "unlockedAt", column = "unlocked_at"),
            @Result(property = "isPrimary", column = "is_primary"),
            @Result(property = "characterStatus", column = "character_status"),
            @Result(property = "listedAt", column = "listed_at")
    })
    CharacterInstance findByCharacterId(String characterId);

    @Select("SELECT * FROM characters WHERE user_id = #{userId} ORDER BY unlocked_at")
    @Results({
            @Result(property = "characterId", column = "character_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "characterTemplateId", column = "character_template_id"),
            @Result(property = "unlockedAt", column = "unlocked_at"),
            @Result(property = "isPrimary", column = "is_primary"),
            @Result(property = "characterStatus", column = "character_status"),
            @Result(property = "listedAt", column = "listed_at")
    })
    List<CharacterInstance> findOwnedCharacters(String userId);

    @Select("SELECT * FROM characters WHERE user_id = #{userId} AND is_primary = TRUE")
    @Results({
            @Result(property = "characterId", column = "character_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "characterTemplateId", column = "character_template_id"),
            @Result(property = "unlockedAt", column = "unlocked_at"),
            @Result(property = "isPrimary", column = "is_primary"),
            @Result(property = "characterStatus", column = "character_status"),
            @Result(property = "listedAt", column = "listed_at")
    })
    CharacterInstance findPrimaryCharacter(String userId);

    @Select("SELECT * FROM characters " +
            "WHERE character_status = 'LISTED_FOR_BATTLE' AND user_id != #{userId} " +
            "ORDER BY listed_at")
    @Results({
            @Result(property = "characterId", column = "character_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "characterTemplateId", column = "character_template_id"),
            @Result(property = "unlockedAt", column = "unlocked_at"),
            @Result(property = "isPrimary", column = "is_primary"),
            @Result(property = "characterStatus", column = "character_status"),
            @Result(property = "listedAt", column = "listed_at")
    })
    List<CharacterInstance> findListedChallengers(String currentUserId);


    @Update("UPDATE characters SET is_primary = TRUE WHERE character_id = #{characterId}")
    void setPrimaryCharacter(String characterId);

    @Update("UPDATE characters SET is_primary = FALSE, character_status = 'IDLE', listed_at = null WHERE character_id != #{characterId} AND user_id = #{userId}")
    void setSecondaryCharacter(String characterId, String userId);

    @Update("UPDATE characters SET character_status = #{characterStatus}, listed_at = #{listedAt} WHERE character_id = #{characterId}")
    void updateStatus(CharacterInstance characterInstance);

}
