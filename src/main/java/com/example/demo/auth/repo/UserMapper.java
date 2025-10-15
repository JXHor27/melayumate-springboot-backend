package com.example.demo.auth.repo;

import org.apache.ibatis.annotations.*;
import com.example.demo.auth.entity.UserEntity;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO users(user_id, username, password, email, role) VALUES(#{userId}, #{username}, #{password}, #{email}, #{role})")
    void insertUser(UserEntity user);

    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "dailyGoal", column = "daily_goal")
    })
    UserEntity getUserByUserId(String userId);

    @Select("SELECT * FROM users WHERE username = #{username}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "dailyGoal", column = "daily_goal")
    })
    UserEntity getUserByUsername(String username);

    @Select("SELECT * FROM users WHERE email = #{email}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "dailyGoal", column = "daily_goal")
    })
    UserEntity getUserByEmail(String email);

    @Update("UPDATE users SET password = #{password} WHERE user_id = #{userId}")
    void updatePassword(UserEntity user);
}