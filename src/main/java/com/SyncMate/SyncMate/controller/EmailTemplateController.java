package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.dto.emailTemplates.EmailTemplateDto;
import com.SyncMate.SyncMate.dto.emailTemplates.RequestEmailTemplate;
import com.SyncMate.SyncMate.dto.emailTemplates.responses.EmailTemplateResponse;
import com.SyncMate.SyncMate.dto.emailTemplates.responses.EmailTemplatesListResponse;
import com.SyncMate.SyncMate.services.EmailTemplateService;
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
@RequestMapping("email-templates")
public class EmailTemplateController {

    @Autowired
    private EmailTemplateService emailTemplateService;

    @Operation(summary = "Save new email template", description = "saving email templates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved the email template",
                    content = @Content(schema = @Schema(implementation = EmailTemplateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PostMapping
    public ResponseEntity<MakeResponseDto<EmailTemplateDto>> saveEmailTemplate(@Valid @RequestBody RequestEmailTemplate requestEmailTemplate) {
        EmailTemplateDto emailTemplate = emailTemplateService.saveEmailTemplate(requestEmailTemplate);
        MakeResponseDto<EmailTemplateDto> finalResponse = new MakeResponseDto<>(true, "Saved Email Template", emailTemplate);
        return ResponseEntity.ok(finalResponse);
    }

    @Operation(summary = "Get email template by ID", description = "Fetch a specific email template by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the email template",
                    content = @Content(schema = @Schema(implementation = EmailTemplateResponse.class))),
            @ApiResponse(responseCode = "404", description = "Email template not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<MakeResponseDto<EmailTemplateDto>> getEmailTemplate(@PathVariable Long id) {
        EmailTemplateDto emailTemplate = emailTemplateService.getEmailTemplate(id);
        MakeResponseDto<EmailTemplateDto> finalResponse = new MakeResponseDto<>(true, "Successfully fetched the Email Template", emailTemplate);
        return ResponseEntity.ok(finalResponse);
    }

    @Operation(summary = "Get all user email templates", description = "Fetch all email templates belonging to the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched templates",
                    content = @Content(schema = @Schema(implementation = EmailTemplatesListResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @GetMapping("user-email-templates")
    public ResponseEntity<MakeResponseDto<List<EmailTemplateDto>>> getUserEmailTemplates() {
        List<EmailTemplateDto> templates = emailTemplateService.getUserEmailTemplates();
        MakeResponseDto<List<EmailTemplateDto>> response = new MakeResponseDto<>(true, "Fetched all email templates", templates);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update an email template", description = "Update an existing email template by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the email template",
                    content = @Content(schema = @Schema(implementation = EmailTemplateResponse.class))),
            @ApiResponse(responseCode = "404", description = "Email template not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<MakeResponseDto<EmailTemplateDto>> updateEmailTemplate(@PathVariable Long id, @Valid @RequestBody RequestEmailTemplate requestEmailTemplate) {
        EmailTemplateDto updated = emailTemplateService.updateEmailTemplate(id, requestEmailTemplate);
        MakeResponseDto<EmailTemplateDto> response = new MakeResponseDto<>(true, "Updated Email Template", updated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete an email template", description = "Delete a specific email template by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the email template", content = @Content(schema = @Schema(implementation = MakeResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Email template not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MakeResponseDto<?>> deleteEmailTemplate(@PathVariable Long id) {
        emailTemplateService.deleteEmailTemplate(id);
        MakeResponseDto<?> response = new MakeResponseDto<>(true, "Deleted Email Template", null);
        return ResponseEntity.ok(response);
    }
}
