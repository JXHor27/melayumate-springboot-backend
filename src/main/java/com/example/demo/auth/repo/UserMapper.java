package com.example.demo.auth.repo;

import com.example.demo.enums.Role;
import org.apache.ibatis.annotations.*;
import com.example.demo.auth.entity.UserEntity;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO users(user_id, username, password, email, role) VALUES(#{userId}, #{username}, #{password}, #{email}, #{role})")
    void insertUser(UserEntity user);

    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id")
    })
    UserEntity getUserByUserId(String userId);

    @Select("SELECT * FROM users WHERE username = #{username}")
    @Results({
            @Result(property = "userId", column = "user_id")
    })
    UserEntity getUserByUsername(String username);

    @Select("SELECT * FROM users WHERE email = #{email}")
    @Results({
            @Result(property = "userId", column = "user_id")
    })
    UserEntity getUserByEmail(String email);

    @Update("UPDATE users SET password = #{password} WHERE user_id = #{userId}")
    void updatePassword(UserEntity user);

    @Select("SELECT COUNT(*) > 0 FROM users WHERE role = #{role}")
    boolean existsByRole(Role role);

    @Select("SELECT user_id FROM users WHERE role = 'ROLE_USER' ORDER BY user_id")
    @Results({
            @Result(property = "userId", column = "user_id")
    })
    List<String> findAllUserIds();
}