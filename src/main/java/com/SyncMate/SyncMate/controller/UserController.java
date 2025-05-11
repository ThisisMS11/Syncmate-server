package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.UserContactsResponse;
import com.SyncMate.SyncMate.entity.File;
import com.SyncMate.SyncMate.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get all users contacts", description = "Returns a list of all users contacts in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users contacts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserContactsResponse.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema()))
    })
    @GetMapping("/contacts")
    public ResponseEntity<List<UserContactsResponse>> getUserContacts() {
        List<UserContactsResponse> contacts = userService.getUserContacts();
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @Operation(summary = "Get all users files", description = "Returns a list of all users files in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users files",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = File.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema()))
    })
    @GetMapping("/files")
    public ResponseEntity<List<File>> getUserFiles() {
        List<File> files = userService.getUserFiles();
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

}
