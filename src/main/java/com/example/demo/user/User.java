package com.example.demo.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetailsService;

@Getter
@Setter
@ToString
public class User {
    private int userId;
    private String username;
    private String email;
    private String password;

    private int flashcardDone;
    private int lessonDone;
    private int scenarioDone;
    private int currentLevel;
    private int currentEXP;
}
