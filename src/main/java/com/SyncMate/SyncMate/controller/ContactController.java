package com.SyncMate.SyncMate.controller;
import com.SyncMate.SyncMate.dto.ContactDto;
import com.SyncMate.SyncMate.services.ContactService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<?> saveContact(@Valid @RequestBody ContactDto contactDto){
        contactService.saveContact(contactDto);
        return ResponseEntity.ok("Contact saved successfully");
    }

    @PutMapping
    public ResponseEntity<?> updateContact(@RequestBody ContactDto contactDto){
        contactService.saveContact(contactDto);
        return ResponseEntity.ok("Contact Updated successfully");
    }
}
