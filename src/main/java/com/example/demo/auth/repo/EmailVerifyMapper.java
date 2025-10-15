package com.example.demo.auth.repo;

import com.example.demo.auth.entity.EmailVerificationToken;
import com.example.demo.auth.entity.PasswordResetToken;
import org.apache.ibatis.annotations.*;

@Mapper
public interface EmailVerifyMapper {
    @Insert("INSERT INTO email_verify_tokens(token_id, email, token_hash, expires_at) VALUES(#{tokenId}, #{email}, #{tokenHash}, #{expiresAt})")
    void saveToken(EmailVerificationToken emailVerificationToken);

    @Select("SELECT * FROM email_verify_tokens WHERE email = #{email}" )
    @Results({
            @Result(property = "tokenId", column = "token_id"),
            @Result(property = "tokenHash", column = "token_hash"),
            @Result(property = "expiresAt", column = "expires_at"),
    })
    EmailVerificationToken getTokenByEmail(String email);

    @Delete("DELETE FROM email_verify_tokens WHERE email = #{email}")
    void deleteTokenByEmail(String email);
}
