package com.example.demo.auth.controller;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.entity.UserEntity;
import com.example.demo.auth.service.EmailVerificationService;
import com.example.demo.auth.service.JwtService;

import com.example.demo.auth.service.PasswordResetService;
import com.example.demo.auth.service.UserManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "User Authentication", description = "APIs for managing users auth")
public class AuthController {

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final UserDetailsService userDetailsService;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final UserManagementService userService;

    @Autowired
    private final PasswordResetService passwordResetService;

    @Autowired
    private final EmailVerificationService emailVerificationService;

    /**
     * Authenticate user and generate JWT token.
     *
     * @param authenticationRequest the authentication request containing username and password
     * @return a ResponseEntity containing the JWT token or an error message
     * @throws Exception if authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    /**
     * Verify username and email, then proceed to send email verification code.
     *
     * @param registerRequest the registration request containing username, email, and password
     */
    @PostMapping("/verify-email")
    public void verifyUserCredentials(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.verifyUserCredentials(registerRequest);
    }

    /**
     * Verify email verification code.
     *
     * @param verifyCodeRequest the request containing email and token
     * @return a ResponseEntity with a success message or an error message
     */
    @PostMapping("/verify-email-code")
    public ResponseEntity<Map<String, String>> verifyEmailCode(@Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
        boolean isTokenValid = emailVerificationService.validateEmailVerificationToken(verifyCodeRequest.getEmail(), verifyCodeRequest.getCode());

        if (!isTokenValid) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired code."));
        }

        return ResponseEntity.ok(Map.of("message", "Correct code."));
    }


    /**
     * Register a new user.
     *
     * @param registerRequest the registration request containing username, email, and password
     * @return a ResponseEntity containing the created UserEntity or an error message
     */
    @PostMapping("/register")
    public ResponseEntity<UserEntity> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserEntity registeredUser = userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    /**
     * Handle forgot password requests.
     *
     * @param payload a map containing the email address
     * @return a ResponseEntity with a generic success message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        passwordResetService.generateAndSendResetToken(email);

        // ALWAYS return a generic success message to prevent user enumeration
        return ResponseEntity.ok(Map.of("message", "If an account with that email address exists, a password reset code has been sent."));
    }

    /**
     * Verify the password reset code.
     *
     * @param verifyCodeRequest the request containing email and token
     * @return a ResponseEntity with a success message or an error message
     */
    @PostMapping("/verify-reset-code")
    public ResponseEntity<Map<String, String>> verifyCode(@Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
        boolean isTokenValid = passwordResetService.validatePasswordResetToken(verifyCodeRequest.getEmail(), verifyCodeRequest.getCode());

        if (!isTokenValid) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired code."));
        }

        return ResponseEntity.ok(Map.of("message", "Correct code."));
    }


    /**
     * Handle password reset requests.
     *
     * @param passwordResetRequest the password reset request containing email, token, and new password
     * @return a ResponseEntity with a success message or an error message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
         boolean isTokenValid = passwordResetService.validatePasswordResetToken(passwordResetRequest.getEmail(), passwordResetRequest.getCode());

         if (!isTokenValid) {
             return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired code."));
         }

         userService.updatePassword(passwordResetRequest);

        return ResponseEntity.ok(Map.of("message", "Password has been reset successfully."));
    }

    /**
     * Change the password for an authenticated user.
     *
     * @param passwordChangeRequest the password change request containing userId, old password, and new password
     * @return a ResponseEntity with a success message
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        userService.changePassword(passwordChangeRequest);
        return ResponseEntity.ok(Map.of("message", "Password has been changed successfully."));
    }

    /**
     * Retrieve user details by user ID.
     *
     * @param userId the ID of the user to retrieve
     * @return a ResponseEntity containing the UserEntity
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserEntity> getUserDetails(@PathVariable String userId) {
        UserEntity user = userService.findUserById(userId);
        return ResponseEntity.ok(user);
    }


}
