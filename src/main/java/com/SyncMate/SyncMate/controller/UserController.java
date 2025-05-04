package com.SyncMate.SyncMate.controller;
import com.SyncMate.SyncMate.dto.UserContactsResponse;
import com.SyncMate.SyncMate.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/contacts")
    public ResponseEntity<?> getUserContacts(){
        List<UserContactsResponse> contacts = userService.getUserContacts();
        if(contacts!=null && !contacts.isEmpty()){
            return new ResponseEntity<>(contacts, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
