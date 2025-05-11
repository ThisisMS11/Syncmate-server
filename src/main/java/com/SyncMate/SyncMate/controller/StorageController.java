package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.UserContactsResponse;
import com.SyncMate.SyncMate.entity.File;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.exception.ApiError;
import com.SyncMate.SyncMate.services.FileService;
import com.SyncMate.SyncMate.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Upload file", description = "Returns the uploaded file metadata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully uploaded the file",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = File.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request please recheck the input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());
        File uploadedFile = fileService.uploadFile(file, user);
        return ResponseEntity.ok(uploadedFile);
    }

    @Operation(summary = "Download file", description = "Returns the byte data for a file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the file data",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request please recheck the input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        byte[] fileData = fileService.downloadFile(fileId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/octet-stream")
                .body(fileData);
    }


    @Operation(summary = "Delete file", description = "Deletes the requested file with some ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the file", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Forbidden Access",content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "File not found",content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request please recheck the input",content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());
        boolean deleted = fileService.deleteFile(fileId, user);
        return deleted ?
                ResponseEntity.ok("File deleted") :
                ResponseEntity.notFound().build();
    }
}