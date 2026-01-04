package com.example.demo.service;

import com.example.demo.auth.entity.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.auth.repo.UserMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes a default admin user if no lecturer account exists.
 */
@Component
public class AdminUserInitializer implements CommandLineRunner {

    private static final Log logger = LogFactory.getLog(AdminUserInitializer.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IdGeneratorService idGeneratorService;

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_EMAIL = "melayumate@gmail.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "MelayuMate123!";

    @Override
    public void run(String... args) throws Exception {
        // check if a LECTURER role already exists. If so, we do nothing.
        if (userMapper.existsByRole(Role.ROLE_LECTURER)) {
            logger.info("Lecturer account already exists. Skipping initialization.");
            return;
        }
        logger.info("No lecturer account found. Creating super admin...");
        UserEntity superAdmin = new UserEntity();
        superAdmin.setUserId(idGeneratorService.generateUserId());
        superAdmin.setUsername(DEFAULT_ADMIN_USERNAME);
        superAdmin.setEmail(DEFAULT_ADMIN_EMAIL);
        superAdmin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
        superAdmin.setRole(Role.ROLE_LECTURER);
        userMapper.insertUser(superAdmin);
        logger.info("Super admin created with username: " + DEFAULT_ADMIN_USERNAME + " and email: " + DEFAULT_ADMIN_EMAIL);
    }
}