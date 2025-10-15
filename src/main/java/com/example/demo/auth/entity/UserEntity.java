package com.example.demo.auth.entity;

import com.example.demo.enums.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserEntity {
    private String userId;
    private String username;
    private String email;
    private String password;
    private Role role;
    private int dailyGoal;
}
