package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.UserContactDto;
import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.dto.responses.contact.UserContactsResponse;
import com.SyncMate.SyncMate.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cron")
public class CronController {

    @Autowired
    private EmailService emailService;

    @Operation(summary = "Send Scheduled Emails", description = "Sends all the scheduled emails")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully sent the scheduled emails",
                    content = @Content(schema = @Schema(implementation = MakeResponseDto.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = MakeResponseDto.class)))
    })
    @GetMapping("/send-scheduled-emails")
    public ResponseEntity<MakeResponseDto<?>> sendScheduledEmails() {
        emailService.SendScheduledEmails();
        MakeResponseDto<?> finalResponse = new MakeResponseDto<>(true, "Sent Scheduled emails successfully", null);
        return ResponseEntity.ok(finalResponse);
    }
}
