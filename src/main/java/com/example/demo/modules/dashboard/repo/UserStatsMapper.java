package com.example.demo.modules.dashboard.repo;

import com.example.demo.modules.dashboard.entity.UserStats;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserStatsMapper {

    @Insert("INSERT INTO user_stats(user_id, username, daily_goal, current_level, current_exp) VALUES(#{userId}, #{username}, #{dailyGoal}, #{currentLevel}, #{currentExp})")
    void createInitialStats(UserStats userStats);

    @Update("UPDATE user_stats SET daily_goal = #{dailyGoal} WHERE user_id = #{userId}")
    void updateDailyGoal(UserStats userStats);

    @Select("SELECT * FROM user_stats WHERE daily_goal > 0 ORDER BY user_id ASC")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "username", column = "username"),
            @Result(property = "dailyGoal", column = "daily_goal"),
            @Result(property = "currentLevel", column = "current_level"),
            @Result(property = "currentExp", column = "current_exp")
    })
    List<UserStats> findAllWithDailyGoal();

    @Select("SELECT * FROM user_stats WHERE user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "username", column = "username"),
            @Result(property = "dailyGoal", column = "daily_goal"),
            @Result(property = "currentLevel", column = "current_level"),
            @Result(property = "currentExp", column = "current_exp"),
    })
    UserStats getUserStatsByUserId(String userId);

    @Update("UPDATE user_stats SET current_level = #{currentLevel}, current_exp = #{currentExp} WHERE user_id = #{userId}")
    void updateUserStats(UserStats userStats);


}
