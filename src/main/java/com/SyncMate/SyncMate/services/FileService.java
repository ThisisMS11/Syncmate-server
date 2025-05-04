package com.SyncMate.SyncMate.services;
import java.io.IOException;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    // Upload a file to GCS
    public File uploadFile(MultipartFile file, User user) {
        log.info("Received file upload request");

        if (file == null || file.isEmpty()) {
            log.warn("Uploaded file is null or empty");
            throw new IllegalArgumentException("Uploaded file is empty or null");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            log.warn("Uploaded file has no original filename");
            throw new IllegalArgumentException("File must have a valid name");
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
        File savedFile = new File();
        savedFile.setOriginalFilename(originalName);
        savedFile.setGcsFilename(gcsFilename);
        savedFile.setPublicUrl(publicUrl);
        savedFile.setContentType(file.getContentType());
        savedFile.setSize(file.getSize());
        savedFile.setBucketName(bucketName);
        savedFile.setFileType(FileType.detectFromContentType(file.getContentType()));
        savedFile.setUser(user);

        return fileRepository.save(savedFile);
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
                    return CommonExceptions.resourceNotFound("File ID: " + fileId);
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
    public boolean deleteFile(Long fileId) {
        log.info("Attempting to delete file with ID: {}", fileId);

        if (fileId == null) {
            log.warn("Null fileId provided for deletion");
            throw CommonExceptions.invalidRequest("File ID must not be null");
        }

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> {
                    log.warn("No file found in DB with ID: {}", fileId);
                    return GcsException.fileNotFound("File ID: " + fileId);
                });

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
}
