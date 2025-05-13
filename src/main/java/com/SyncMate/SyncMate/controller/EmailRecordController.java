package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.email.EmailRecordRequestDto;
import com.SyncMate.SyncMate.dto.responses.emailRecords.EmailRecordListResponse;
import com.SyncMate.SyncMate.entity.EmailRecord;
import com.SyncMate.SyncMate.services.EmailRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Saving email records", description = "Saving email records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saving the email record",
                    content = @Content(schema = @Schema(implementation = EmailRecordListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PostMapping
    public ResponseEntity<EmailRecordListResponse> saveEmailRecords(@Valid @RequestBody EmailRecordRequestDto emailRecordRequestDto) {
        List<EmailRecord> emailRecordList = emailRecordService.saveEmailRecords(emailRecordRequestDto);
        EmailRecordListResponse emailRecordListResponse = new EmailRecordListResponse(true, "Created email records successfully", emailRecordList);
        return ResponseEntity.ok(emailRecordListResponse);
    }

    @Operation(summary = "Fetching email records", description = "Fetching email records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetching the user email records",
                    content = @Content(schema = @Schema(implementation = EmailRecordListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @GetMapping
    public ResponseEntity<EmailRecordListResponse> getUserEmailRecords() {
        List<EmailRecord> emailRecordList = emailRecordService.getUserEmailRecords();
        EmailRecordListResponse emailRecordListResponse = new EmailRecordListResponse(true, "Fetched email records successfully", emailRecordList);
        return ResponseEntity.ok(emailRecordListResponse);
    }
}
