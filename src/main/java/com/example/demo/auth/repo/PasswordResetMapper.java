package com.example.demo.auth.repo;

import com.example.demo.auth.entity.PasswordResetToken;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PasswordResetMapper {
    @Insert("INSERT INTO password_reset_tokens(token_id, user_id, token_hash, expires_at) VALUES(#{tokenId}, #{userId}, #{tokenHash}, #{expiresAt})")
    void saveToken(PasswordResetToken passwordResetToken);

    @Select("SELECT * FROM password_reset_tokens WHERE user_id = #{userId}" )
    @Results({
            @Result(property = "tokenId", column = "token_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "tokenHash", column = "token_hash"),
            @Result(property = "expiresAt", column = "expires_at"),
    })
    PasswordResetToken getTokenByUserId(String userId);

    @Delete("DELETE FROM password_reset_tokens WHERE user_id = #{userId}")
    void deleteTokenByUserId(String userId);
}
