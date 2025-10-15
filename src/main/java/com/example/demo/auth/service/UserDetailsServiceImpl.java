package com.example.demo.auth.service;

import com.example.demo.auth.entity.UserEntity;
import com.example.demo.auth.model.CustomUserDetails;
import com.example.demo.auth.repo.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Log logger = LogFactory.getLog(UserDetailsServiceImpl.class);

    @Autowired
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username){
        UserEntity user = userMapper.getUserByUsername(username);
        logger.info("Loading user: "+user);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        String password = user.getPassword();
        return CustomUserDetails.create(user);
    }
}
