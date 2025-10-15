package com.example.demo.dashboard.repo;

import com.example.demo.dashboard.entity.UserStats;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserStatsMapper {

    @Insert("INSERT INTO user_stats(user_id, daily_goal, current_level, current_exp) VALUES(#{userId}, #{dailyGoal}, #{currentLevel}, #{currentExp})")
    void createInitialStats(UserStats userStats);

    @Update("UPDATE user_stats SET daily_goal = #{dailyGoal} WHERE user_id = #{userId}")
    void updateGoal(UserStats userStats);

    @Select("SELECT * FROM user_stats WHERE daily_goal > 0")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "dailyGoal", column = "daily_goal"),
            @Result(property = "currentLevel", column = "current_level"),
            @Result(property = "currentExp", column = "current_exp")
    })
    List<UserStats> findAllWithDailyGoal();

    @Select("SELECT * FROM user_stats WHERE user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "dailyGoal", column = "daily_goal"),
            @Result(property = "currentLevel", column = "current_level"),
            @Result(property = "currentExp", column = "current_exp")
    })
    UserStats getUserStatsByUserId(String userId);

}
