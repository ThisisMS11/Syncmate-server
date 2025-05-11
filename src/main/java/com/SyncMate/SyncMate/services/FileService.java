package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.entity.File;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.enums.FileType;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.exception.GcsException;
import com.SyncMate.SyncMate.repository.FileRepository;
import com.google.cloud.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    @Autowired
    private Storage storage;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserService userService;

    public File getFileById(Long fileId){
        log.info("Attempting to fetch file with ID: {}", fileId);

        if (fileId == null) {
            log.warn("Null fileId provided");
            throw CommonExceptions.invalidRequest("File ID must not be null");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());

        File file = fileRepository.findById(fileId).orElseThrow(() -> {
            log.warn("No file found in DB with ID: {}", fileId);
            throw  CommonExceptions.resourceNotFound("File ID: " + fileId);
        });

        if(!file.getUser().equals(user)){
            throw CommonExceptions.forbiddenAccess();
        }

        return file;
    }

    // Upload a file to GCS
    public File uploadFile(MultipartFile file, User user) {
        log.info("Received file upload request");

        if (file == null || file.isEmpty()) {
            log.warn("Uploaded file is null or empty");
            throw CommonExceptions.invalidRequest("Uploaded file is empty or null");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            log.warn("Uploaded file has no original filename");
            throw CommonExceptions.invalidRequest("File must have a valid name");
        }

        if (user == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user = userService.getUserByEmail(authentication.getName());
        }

        String gcsFilename = UUID.randomUUID() + "_" + originalName;
        log.info("Generated unique filename: {}", gcsFilename);

        BlobId blobId = BlobId.of(bucketName, gcsFilename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        try {
            Blob blob = storage.create(blobInfo, file.getBytes());
            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
            log.info("File uploaded successfully to bucket: {}, file: {}", bucketName, gcsFilename);
        } catch (IOException e) {
            log.error("Failed to upload file to GCS", e);
            throw GcsException.uploadFailed(gcsFilename);
        }

        String publicUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, gcsFilename);

        // Build entity
        try {
            File savedFile = new File();
            savedFile.setOriginalFilename(originalName);
            savedFile.setGcsFilename(gcsFilename);
            savedFile.setPublicUrl(publicUrl);
            savedFile.setContentType(file.getContentType());
            savedFile.setSize(file.getSize());
            savedFile.setBucketName(bucketName);
            savedFile.setFileType(FileType.detectFromContentType(file.getContentType()));
            savedFile.setUser(user);

            log.info("Saving file into DB");
            return fileRepository.save(savedFile);

        } catch (Exception e) {
            log.error("Error saving file into DB : {}", e.getMessage());
            storage.delete(bucketName, gcsFilename);
            throw CommonExceptions.operationFailed("Error saving file into DB");
        }
    }


    // Download a file from GCS using file ID
    public byte[] downloadFile(Long fileId) {
        log.info("Attempting to download file with ID: {}", fileId);

        if (fileId == null) {
            log.warn("Null fileId provided for download");
            throw CommonExceptions.invalidRequest("File ID must not be null");
        }

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> {
                    log.warn("No file found in DB with ID: {}", fileId);
                    throw CommonExceptions.resourceNotFound("File ID: " + fileId);
                });

        String gcsFilename = file.getGcsFilename();
        String bucket = file.getBucketName() != null ? file.getBucketName() : bucketName;

        log.info("Fetching file from GCS: bucket={}, filename={}", bucket, gcsFilename);

        Blob blob = storage.get(bucket, gcsFilename);
        if (blob == null || !blob.exists()) {
            log.warn("File not found in GCS: {}", gcsFilename);
            throw CommonExceptions.resourceNotFound(gcsFilename);
        }

        byte[] content = blob.getContent();
        log.info("File downloaded successfully: {}", gcsFilename);
        return content;
    }


    // Delete a file from GCS and database
    public boolean deleteFile(Long fileId, User user) {
        log.info("Attempting to delete file with ID: {}", fileId);

        if (fileId == null) {
            log.warn("Null fileId provided for deletion");
            throw CommonExceptions.invalidRequest("File ID must not be null");
        }

        if (user == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user = userService.getUserByEmail(authentication.getName());
        }

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> {
                    log.warn("No file found in DB with ID: {}", fileId);
                    throw GcsException.fileNotFound("File ID: " + fileId);
                });

        if (!file.getUser().equals(user)) {
            log.error("User is not authorized to delete the file");
            throw CommonExceptions.forbiddenAccess();
        }

        String gcsFilename = file.getGcsFilename();
        String bucket = file.getBucketName() != null ? file.getBucketName() : bucketName;

        log.info("Deleting file from GCS: bucket={}, filename={}", bucket, gcsFilename);

        try {
            boolean deletedFromGcs = storage.delete(bucket, gcsFilename);
            if (!deletedFromGcs) {
                log.warn("File not found in GCS: {}", gcsFilename);
                throw GcsException.fileNotFound(gcsFilename);
            }

            log.info("File deleted from GCS successfully: {}", gcsFilename);

            fileRepository.deleteById(fileId);
            log.info("File metadata deleted from DB for ID: {}", fileId);

            return true;
        } catch (Exception e) {
            log.error("Failed to delete file: {}", gcsFilename, e);
            throw GcsException.deleteFailed(gcsFilename);
        }
    }

    public boolean existsById(Long id){
        log.info("Validating the existence of file with id : {}", id);
        return fileRepository.existsById(id);
    }
}
