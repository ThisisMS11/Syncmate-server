package com.SyncMate.SyncMate.controller;
import com.SyncMate.SyncMate.entity.File;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.services.FileService;
import com.SyncMate.SyncMate.services.UserService;
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

    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());
        File uploadedFile = fileService.uploadFile(file,user);
        return ResponseEntity.ok(uploadedFile);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        byte[] fileData = fileService.downloadFile(fileId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/octet-stream")
                .body(fileData);
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        boolean deleted = fileService.deleteFile(fileId);
        return deleted ?
                ResponseEntity.ok("File deleted") :
                ResponseEntity.notFound().build();
    }
}