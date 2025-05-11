package com.SyncMate.SyncMate.controller;
import com.SyncMate.SyncMate.dto.email.EmailRecordDto;
import com.SyncMate.SyncMate.entity.EmailRecord;
import com.SyncMate.SyncMate.repository.EmailRepository;
import com.SyncMate.SyncMate.services.EmailRecordService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/email-records")
public class EmailRecordController {

    @Autowired
    private EmailRecordService emailRecordService;

    @PostMapping
    public ResponseEntity<?> saveEmailRecords(@Valid @RequestBody EmailRecordDto emailRecordDto){
        emailRecordService.saveEmailRecords(emailRecordDto);
        return ResponseEntity.ok("Email Records Created Successfully");
    }

    @GetMapping
    public ResponseEntity<?> saveUserEmailRecords(){
        List<EmailRecord> emailRecordList = emailRecordService.getUserEmailRecords();
        return ResponseEntity.ok(emailRecordList);
    }
}
