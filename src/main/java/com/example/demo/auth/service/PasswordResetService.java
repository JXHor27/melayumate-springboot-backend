package com.example.demo.auth.service;


import com.example.demo.auth.entity.PasswordResetToken;
import com.example.demo.auth.entity.UserEntity;
import com.example.demo.auth.repo.PasswordResetMapper;
import com.example.demo.exception.EmailNotFoundException;
import com.example.demo.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Log logger = LogFactory.getLog(PasswordResetService.class);
    private static final int TOKEN_VALIDITY_MINUTES = 5;

    @Autowired
    private final JavaMailSender mailSender;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserManagementService userManagementService;

    @Autowired
    private final PasswordResetMapper passwordResetMapper;

    @Autowired
    private final IdGeneratorService idGeneratorService;

    /**
     * Generates a password reset token, saves it to the database, and sends it via email.
     * @param userEmail The email of the user requesting the password reset.
     */
    @Async
    @Transactional
    public void generateAndSendResetToken(String userEmail) {
        Optional<UserEntity> userOptional = Optional.ofNullable(userManagementService.findUserByEmail(userEmail));
        if (userOptional.isEmpty()) {
             logger.warn("Password reset requested for non-existent email: " + userEmail);
             throw new EmailNotFoundException("Email does not exist: " + userEmail);
        }
        UserEntity user = userOptional.get();

        String userId = user.getUserId();

        // Delete any pre-existing tokens for this user, "One User, One Token" rule
        passwordResetMapper.deleteTokenByUserId(userId);

        String plainTextToken = generateRandomSixDigitCode();
        String hashedToken = passwordEncoder.encode(plainTextToken);
        Instant expiryDate = Instant.now().plusSeconds(TOKEN_VALIDITY_MINUTES * 60);

        // Save hashed token to database
        String tokenId = idGeneratorService.generateResetTokenId();
        PasswordResetToken token = new PasswordResetToken(tokenId, userId, hashedToken, expiryDate);

        passwordResetMapper.saveToken(token);

        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(userEmail);
            email.setSubject("Your MelayuMate Password Reset Code");
            email.setText("Hello,\n\nHere is your password reset code. It is valid for "
                    + TOKEN_VALIDITY_MINUTES + " minutes.\n\nCode: " + plainTextToken
                    + "\n\nIf you did not request this, please ignore this email.");

            mailSender.send(email);
            logger.info("Password reset email sent successfully to: " + userEmail);
        } catch (Exception e) {
            logger.error("Exception occur when sending code to email: " + e);
        }
    }

    /**
     * Validates a password reset token for a given user.
     * @param email The email of the user attempting the reset.
     * @param plainTextToken The 6-digit code submitted by the user.
     * @return true if the token is valid, false otherwise.
     */
    @Transactional
    public boolean validatePasswordResetToken(String email, String plainTextToken) {
        logger.info("Validating reset email: " + email);

        // Find user by their email. If not found, token is invalid
        Optional<UserEntity> userOptional = Optional.ofNullable(userManagementService.findUserByEmail(email));
        if (userOptional.isEmpty()) {
            return false;
        }
        UserEntity user = userOptional.get();

        // Find token associated with this user
        Optional<PasswordResetToken> tokenOptional = Optional.ofNullable(passwordResetMapper.getTokenByUserId(user.getUserId()));
        if (tokenOptional.isEmpty()) {
            return false;
        }
        PasswordResetToken token = tokenOptional.get();

        // Check token expiration
        if (token.getExpiresAt().isBefore(Instant.now())) {
            logger.info("Token expires:" + email);
            passwordResetMapper.deleteTokenByUserId(user.getUserId()); // Clean up expired token
            return false;
        }

        // Compare plain text token with hashed token from database
        return passwordEncoder.matches(plainTextToken, token.getTokenHash());

        // If all checks pass, the token is valid.
    }

    private String generateRandomSixDigitCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }
}