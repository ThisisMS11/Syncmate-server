package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.entity.File;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.enums.FileType;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.repository.FileRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class FileService {

    @Autowired
    private Cloudinary cloudinary; // configured as a @Bean

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UtilService utilService;

    @Value("${cloudinary.folder:syncmate}") // optional folder name
    private String uploadFolder;

    public File getFileById(Long fileId) {
        log.info("Attempting to fetch file with ID: {}", fileId);

        if (fileId == null) {
            log.warn("Null fileId provided");
            throw CommonExceptions.invalidRequest("File ID must not be null");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());

        File file = fileRepository.findById(fileId).orElseThrow(() -> {
            log.warn("No file found in DB with ID: {}", fileId);
            throw CommonExceptions.resourceNotFound("File ID: " + fileId);
        });

        if (utilService.checkResourceAuthorization(file.getUser())) {
            log.error("Access forbidden for file with id : {}", file.getId());
            throw CommonExceptions.forbiddenAccess();
        }

        return file;
    }

    // Upload a file to Cloudinary
    public File uploadFile(MultipartFile multipartFile, User user) {
        log.info("Received file upload request");

        if (multipartFile == null || multipartFile.isEmpty()) {
            log.warn("Uploaded file is null or empty");
            throw CommonExceptions.invalidRequest("Uploaded file is empty or null");
        }

        String originalName = multipartFile.getOriginalFilename();
        if (originalName == null) {
            log.warn("Uploaded file has no original filename");
            throw CommonExceptions.invalidRequest("File must have a valid name");
        }

        if (user == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user = userService.getUserByEmail(authentication.getName());
        }

        try {
            log.info("Uploading file to Cloudinary: {}", originalName);

            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "folder", uploadFolder,
                    "resource_type", "auto", // auto-detect file type
                    "public_id", System.currentTimeMillis() + "_" + originalName
            );

            Map<String, Object> uploadResult = cloudinary.uploader()
                    .upload(multipartFile.getBytes(), uploadOptions);

            log.info("File uploaded successfully to Cloudinary: {}", uploadResult);

            // Build entity
            File savedFile = new File();
            savedFile.setOriginalFilename(originalName);
            savedFile.setPublicId((String) uploadResult.get("public_id"));
            savedFile.setUrl((String) uploadResult.get("url"));
            savedFile.setSecureUrl((String) uploadResult.get("secure_url"));
            savedFile.setFormat((String) uploadResult.get("format"));
            savedFile.setBytes(((Number) uploadResult.get("bytes")).longValue());
            savedFile.setFileType(FileType.detectFromContentType(multipartFile.getContentType()));
            savedFile.setUser(user);

            log.info("Saving file into DB");
            return fileRepository.save(savedFile);

        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary", e);
            throw CommonExceptions.operationFailed("Error uploading file to Cloudinary");
        }
    }

    // Download file is not directly possible from Cloudinary in byte[] easily — return URL
    public String getDownloadUrl(Long fileId) {
        log.info("Attempting to get download URL for file with ID: {}", fileId);

        if (fileId == null) {
            log.warn("Null fileId provided for download");
            throw CommonExceptions.invalidRequest("File ID must not be null");
        }

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> {
                    log.warn("No file found in DB with ID: {}", fileId);
                    throw CommonExceptions.resourceNotFound("File ID: " + fileId);
                });

        if (file.getSecureUrl() == null) {
            throw CommonExceptions.resourceNotFound("No URL available for file: " + fileId);
        }

        return file.getSecureUrl();
    }

    // Delete a file from Cloudinary and database
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
                    throw CommonExceptions.resourceNotFound("File ID: " + fileId);
                });

        if (utilService.checkResourceAuthorization(file.getUser())) {
            log.error("Access forbidden for file with id : {}", file.getId());
            throw CommonExceptions.forbiddenAccess();
        }

        try {
            log.info("Deleting file from Cloudinary: {}", file.getPublicId());

            Map<String, Object> result = cloudinary.uploader()
                    .destroy(file.getPublicId(), ObjectUtils.emptyMap());

            if (!"ok".equals(result.get("result"))) {
                log.warn("File not found in Cloudinary: {}", file.getPublicId());
                throw CommonExceptions.resourceNotFound(file.getPublicId());
            }

            fileRepository.deleteById(fileId);
            log.info("File metadata deleted from DB for ID: {}", fileId);

            return true;
        } catch (Exception e) {
            log.error("Failed to delete file: {}", file.getPublicId(), e);
            throw CommonExceptions.operationFailed("Error deleting file from Cloudinary");
        }
    }

    public boolean existsById(Long id) {
        log.info("Validating the existence of file with id : {}", id);
        return fileRepository.existsById(id);
    }
}
