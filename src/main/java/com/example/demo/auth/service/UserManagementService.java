package com.example.demo.auth.service;

import com.example.demo.auth.dto.PasswordChangeRequest;
import com.example.demo.auth.dto.PasswordResetRequest;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.modules.dashboard.service.StatsService;
import com.example.demo.enums.Role;
import com.example.demo.exception.CredentialExistException;
import com.example.demo.auth.repo.UserMapper;
import com.example.demo.exception.PasswordResetException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.auth.entity.UserEntity;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private static final Log logger = LogFactory.getLog(UserManagementService.class);

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final IdGeneratorService idGeneratorService;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final StatsService statsService;

    @Autowired
    private final EmailVerificationService emailVerificationService;


    public UserEntity findUserByEmail(String email){
        logger.info("Fetching user by email: " + email);
        return userMapper.getUserByEmail(email);
    }

    /**
     * Retrieves user details by user ID, utilizing caching for performance optimization.
     * Only matters the username and email.
     *
     * @param userId the ID of the user to retrieve
     * @return the UserEntity corresponding to the provided userId
     */
    @Cacheable(value = "userDetails", key = "#userId")
    public UserEntity findUserById(String userId) {
        logger.info("Fetching user by ID: " + userId);
        return userMapper.getUserByUserId(userId);
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
            String userId = idGeneratorService.generateUserId();
            newUser.setUserId(userId);
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setRole(Role.ROLE_USER);
            String hashedPassword = passwordEncoder.encode(request.getPassword());
            newUser.setPassword(hashedPassword);
            userMapper.insertUser(newUser);
            statsService.createInitialStats(userId, request.getUsername());
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
        logger.info("Updating password for email: " + request.getEmail());
        UserEntity updatedUser = userMapper.getUserByEmail(request.getEmail());
        updatedUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updatePassword(updatedUser);
        logger.info("Password updated successfully for email: " + request.getEmail());
    }

    public boolean checkUsernameAlreadyRegistered(String username){
        UserEntity storedUser = userMapper.getUserByUsername(username);
        return storedUser != null;
    }

    public boolean checkEmailAlreadyRegistered(String email){
        UserEntity storedUser = userMapper.getUserByEmail(email);
        return storedUser != null;
    }

    @Transactional
    public void changePassword(PasswordChangeRequest passwordChangeRequest) {
        logger.info("Changing password for user id: " + passwordChangeRequest.getUserId());
        UserEntity user = userMapper.getUserByUserId(passwordChangeRequest.getUserId());
        if (user == null) {
            logger.error("User not found with id: " + passwordChangeRequest.getUserId());
            throw new ResourceNotFoundException("User not found with id: " + passwordChangeRequest.getUserId());
        }

        // Verify old password
        if (!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
            logger.error("Old password does not match for user id: " + passwordChangeRequest.getUserId());
            throw new PasswordResetException("Old password is incorrect.");
        }

        // Check new password is different from old password
        if(passwordEncoder.matches(passwordChangeRequest.getNewPassword(), user.getPassword())) {
            logger.error("New password cannot be the same as the old password for user id: " + passwordChangeRequest.getUserId());
            throw new PasswordResetException("New password cannot be the same as the old password.");
        }

        // Check new password and confirmation match
        if(!Objects.equals(passwordChangeRequest.getNewPassword(), passwordChangeRequest.getConfirmNewPassword())) {
            logger.error("New password and confirmation do not match for user id: " + passwordChangeRequest.getUserId());
            throw new PasswordResetException("New password and confirmation do not match.");
        }
        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        userMapper.updatePassword(user);
        logger.info("Password changed successfully for user id: " + passwordChangeRequest.getUserId());
    }
}