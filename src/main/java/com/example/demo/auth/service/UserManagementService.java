package com.example.demo.auth.service;

import com.example.demo.auth.dto.PasswordResetRequest;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.auth.entity.PasswordResetToken;
import com.example.demo.auth.repo.PasswordResetMapper;
import com.example.demo.dashboard.service.StatsService;
import com.example.demo.enums.Role;
import com.example.demo.exception.CredentialExistException;
import com.example.demo.auth.repo.UserMapper;
import com.example.demo.exception.EmailNotFoundException;
import com.example.demo.id.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.auth.entity.UserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private static final Log logger = LogFactory.getLog(UserManagementService.class);
    private static final int TOKEN_VALIDITY_MINUTES = 5;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final IdGenerator idGenerator;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final StatsService statsService;

    @Autowired
    private final EmailVerificationService emailVerificationService;


    public UserEntity findUserByEmail(String email){
        return userMapper.getUserByEmail(email);
    }

    @Transactional
    public void verifyUserCredentials(RegisterRequest request) {
        logger.info("Verifying register request.");
        if(checkUsernameAlreadyRegistered(request.getUsername())){
            logger.error("Username already exists: " + request.getUsername());
            throw new CredentialExistException("Username already exists: " + request.getUsername());
        }
        if(checkEmailAlreadyRegistered(request.getEmail())){
            logger.error("Email already registered: " + request.getEmail());
            throw new CredentialExistException("Email already registered: " + request.getEmail());
        }

        // Send email verification code, logic same as PasswordResetService
        emailVerificationService.generateAndSendVerificationToken(request.getEmail());
    }

    @Transactional
    public UserEntity registerUser(RegisterRequest request) {
        logger.info("Registering user.");


        try {
            UserEntity newUser = new UserEntity();
            String userId = idGenerator.generateUserId();
            newUser.setUserId(userId);
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setDailyGoal(0); // default 0
            newUser.setRole(Role.ROLE_USER);
            String hashedPassword = passwordEncoder.encode(request.getPassword());
            newUser.setPassword(hashedPassword);

            userMapper.insertUser(newUser);
            statsService.createInitialStats(userId);

            logger.info("User registered successfully: " + newUser);
            return newUser;
        }
        catch (DataIntegrityViolationException e) {
            // This will only ever be triggered in the rare event of a race condition.
            // The database constraint prevent data corruption.
            throw new CredentialExistException("An account with this username or email already exists.");
        }
    }

    @Transactional
    public void updatePassword(PasswordResetRequest request) {
        UserEntity updatedUser = findUserByEmail(request.getEmail());
        updatedUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updatePassword(updatedUser);
    }


    public boolean checkUsernameAlreadyRegistered(String username){
        UserEntity storedUser = userMapper.getUserByUsername(username);
        return storedUser != null;
    }

    public boolean checkEmailAlreadyRegistered(String email){
        UserEntity storedUser = userMapper.getUserByEmail(email);
        return storedUser != null;
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