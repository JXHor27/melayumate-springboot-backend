package com.example.demo.user;

import com.example.demo.auth.repo.UserMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.auth.entity.UserEntity;

@Service
public class UserService {

    @Getter
    private UserEntity cachedUser;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserStatMapper userStatMapper;

    public boolean checkUserAlreadyRegistered(UserEntity incomingUser){
        UserEntity storedUser = userMapper.getUserByUsername(incomingUser.getUsername());
        return storedUser != null;
    }

    public void registerUser(UserEntity user) {
        userMapper.insertUser(user);
    }

    public boolean checkEmailAlreadyRegistered(UserEntity incomingUser){
        UserEntity storedUser = userMapper.getUserByEmail(incomingUser.getEmail());
        return storedUser != null;
    }

    // after login, userService instance will cache the currently logged-in user info
    // only for dev stage, since @Autowired userService is a singleton bean
    // thus if in production, several concurrent users will override the cached value
    public void cacheUserInfo(UserEntity user){
        cachedUser = user;
    }

//    public void upsertUserStatFlashcard(User user){
//        user.setUserId(cachedUser.getUserId());
//        userStatMapper.upsertUserStatFlashcard(user);
//    }
//
//    public void upsertUserStatScenario(User user){
//        user.setUserId(cachedUser.getUserId());
//        userStatMapper.upsertUserStatScenario(user);
//    }
//
//    public void upsertUserExp(User user){
//        user.setUserId(cachedUser.getUserId());
//        userStatMapper.upsertUserExp(user);
//    }
//
//
//
//    public User getUserStat(int userId){
//        return userStatMapper.getUserStat(userId);
//    }

}
