package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.ContactDto;
import com.SyncMate.SyncMate.services.ContactService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/add")
    public ResponseEntity<?> saveContact(@Valid @RequestBody ContactDto contactDto){
        contactService.saveContact(contactDto);
        return ResponseEntity.ok("Contact saved successfully");
    }
}
