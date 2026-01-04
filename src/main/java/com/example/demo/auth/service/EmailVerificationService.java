package com.example.demo.auth.service;

import com.example.demo.auth.entity.EmailVerificationToken;
import com.example.demo.auth.repo.EmailVerifyMapper;
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
public class EmailVerificationService {

    private static final Log logger = LogFactory.getLog(EmailVerificationService.class);
    private static final int TOKEN_VALIDITY_MINUTES = 5;

    @Autowired
    private final JavaMailSender mailSender;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final EmailVerifyMapper emailVerifyMapper;

    @Autowired
    private final IdGeneratorService idGeneratorService;

    /**
     * Generates a email verification token, saves it to the database, and sends it via email.
     * @param userEmail The email of the registering user.
     */
    @Async
    @Transactional
    public void generateAndSendVerificationToken(String userEmail) {
        // Delete any pre-existing tokens for this email, "One User, One Token" rule
        emailVerifyMapper.deleteTokenByEmail(userEmail);

        String plainTextToken = generateRandomSixDigitCode();
        String hashedToken = passwordEncoder.encode(plainTextToken);
        Instant expiryDate = Instant.now().plusSeconds(TOKEN_VALIDITY_MINUTES * 60);

        // Save hashed token to database
        String tokenId = idGeneratorService.generateResetTokenId();
        EmailVerificationToken token = new EmailVerificationToken(tokenId, userEmail, hashedToken, expiryDate);

        emailVerifyMapper.saveToken(token);

        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(userEmail);
            email.setSubject("Your MelayuMate Email Verification Code");
            email.setText("Hello,\n\nHere is your email verification code. It is valid for "
                    + TOKEN_VALIDITY_MINUTES + " minutes.\n\nCode: " + plainTextToken
                    + "\n\nIf you did not request this, please ignore this email.");

            mailSender.send(email);
            logger.info("Email verification code sent successfully to: " + userEmail);
        } catch (Exception e) {
            logger.error("Exception occur when sending code to email: " + e);
        }
    }

    /**
     * Validates email verification code for a given user.
     * @param email The email of the user attempting the registration.
     * @param plainTextToken The 6-digit code submitted by the user.
     * @return true if the token is valid, false otherwise.
     */
    @Transactional
    public boolean validateEmailVerificationToken(String email, String plainTextToken) {
        logger.info("Validating email: " + email);

        // Find token associated with this email
        Optional<EmailVerificationToken> tokenOptional = Optional.ofNullable(emailVerifyMapper.getTokenByEmail(email));
        if (tokenOptional.isEmpty()) {
            return false;
        }
        EmailVerificationToken token = tokenOptional.get();

        // Check token expiration
        if (token.getExpiresAt().isBefore(Instant.now())) {
            logger.info("Token expires:" + email);
            emailVerifyMapper.deleteTokenByEmail(email); // Clean up expired token
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