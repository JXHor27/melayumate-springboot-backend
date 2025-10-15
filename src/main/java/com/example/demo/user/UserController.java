package com.example.demo.user;

import com.example.demo.auth.repo.UserMapper;
import com.example.demo.user.utility.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.demo.auth.entity.UserEntity;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/id")
    public String getUser(@RequestBody User user){
        System.out.println("Username: " + user.getUsername());
        System.out.println("Password: " + user.getPassword());
        System.out.println("Email: " + user.getEmail());
        return user.getUsername()+" "+user.getEmail()+" "+user.getPassword();
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserEntity user) {

        // Hash the password before saving
        String hashed = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashed);

        if(userService.checkUserAlreadyRegistered(user)){
            return "Username taken";
        }
        if(userService.checkEmailAlreadyRegistered(user)){
            return "Email already registered";
        }

        userMapper.insertUser(user);
        return "Register success";
    }

    @PostMapping("/login")
    public String login(@RequestBody User incomingUser) {
        UserEntity storedUser = userMapper.getUserByEmail(incomingUser.getEmail());

        if (storedUser == null) {
            return "Email not registered";
        }

        if(!PasswordUtil.matches(incomingUser.getPassword(), storedUser.getPassword())){
            return "Invalid credentials";
        }

        userService.cacheUserInfo(storedUser);
        return ""+storedUser.getUserId();
    }
//
//    @GetMapping("/{id}")
//    public User getUserById(@PathVariable int id) {
//        return userMapper.getUserById(id);
//    }
//
//    @PostMapping("/today/stat/flashcard")
//    public void upsertUserStatFlashcard(@RequestBody User user) {
//        userService.upsertUserStatFlashcard(user);
//    }
//
//    @PostMapping("/today/stat/scenario")
//    public void upsertUserStatScenario(@RequestBody User user) {
//        userService.upsertUserStatScenario(user);
//    }
//
//    @PostMapping("/today/stat/exp")
//    public void upsertUserExp(@RequestBody User user) {
//        userService.upsertUserExp(user);
//    }
//
//    @GetMapping("/today/stat/{userId}")
//    public ResponseEntity<User> getUserStat(@PathVariable int userId) {
//        User userStat = userService.getUserStat(userId);
//        return ResponseEntity.ok(userStat);
//    }
}
