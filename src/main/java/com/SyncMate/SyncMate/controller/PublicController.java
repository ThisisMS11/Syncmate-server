package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    private ResponseEntity<String> registerUser(@RequestBody User user){
        userService.saveNewUser(user);
        return ResponseEntity.ok("User registered successfully");
    }
}
