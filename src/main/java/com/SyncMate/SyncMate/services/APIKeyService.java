package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.dto.APIKey.APIKeyRequestDto;
import com.SyncMate.SyncMate.dto.APIKey.APIKeyResponseDto;
import com.SyncMate.SyncMate.entity.APIkey;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.enums.ExpiryBucket;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.repository.APIKeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@Slf4j
public class APIKeyService {

    @Autowired
    private APIKeyRepository apiKeyRepository;

    @Autowired
    private UtilService utilService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    public APIKeyResponseDto createAPIKey(APIKeyRequestDto apiKeyRequestDto) {
        log.info("Starting to create API key...");

        ExpiryBucket expiryBucket = apiKeyRequestDto.getExpiryBucket();
        if (expiryBucket == null) {
            expiryBucket = ExpiryBucket.ONE_WEEK;
            log.info("Expiry bucket not provided, defaulting to ONE_WEEK.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Fetching user with email: {}", authentication.getName());
        User user = userService.getUserByEmail(authentication.getName());

        try {
            log.info("Generating hashed API key...");
            APIkey apiKey = new APIkey();
            apiKey.setName(apiKeyRequestDto.getName());
            apiKey.setDescription(apiKeyRequestDto.getDescription());
            if (apiKeyRequestDto.getLevel() != null) {
                apiKey.setLevel(apiKeyRequestDto.getLevel());
                log.info("Setting API key level: {}", apiKeyRequestDto.getLevel());
            }

            long expiryMillis = utilService.bucketToExpiryTimestamp(expiryBucket);
            log.info("Calculated expiry timestamp: {}", expiryMillis);
            apiKey.setExpiryTimestamp(expiryMillis);
            apiKey.setApiKeyHash(createRandomAPIKey(user, expiryBucket, expiryMillis));
            apiKey.setUser(user);

            log.info("Saving API key to database...");
            APIkey savedKey = apiKeyRepository.save(apiKey);
            return modelMapper.map(savedKey, APIKeyResponseDto.class);
        } catch (Exception e) {
            log.error("Error while saving API key: {}", e.getMessage(), e);
            throw CommonExceptions.operationFailed("Saving API key failed: " + e.getMessage());
        }
    }

    public void deleteAPIKey(Long apiKeyId) {
        log.info("Attempting to delete API key with ID: {}", apiKeyId);

        APIkey apiKey = apiKeyRepository.findById(apiKeyId)
                .orElseThrow(() -> CommonExceptions.resourceNotFound("API key not found with ID: " + apiKeyId));

        if (utilService.checkResourceAuthorization(apiKey.getUser())) {
            log.warn("User is not authorized to delete API key {}", apiKeyId);
            throw CommonExceptions.unauthorizedAccess();
        }

        apiKeyRepository.delete(apiKey);
        log.info("API key with ID {} successfully deleted.", apiKeyId);
    }

    public boolean validateAPIKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return false;
        }
        APIkey apiKeyEntity = apiKeyRepository.findByApiKeyHash(apiKey);
        if (apiKeyEntity == null) {
            log.error("API Key is not valid");
            return false;
        }

        long currentTimestamp = System.currentTimeMillis();
        if (apiKeyEntity.getExpiryTimestamp() < currentTimestamp) {
            log.error("API Key is expired");
            return false;
        }
        return true;
    }


    private String createRandomAPIKey(User user, ExpiryBucket expiryBucket, long expiryMillis) {
        String rawKey = user.getId() + ":" + expiryMillis + ":" + UUID.randomUUID();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            log.info("API key successfully hashed.");
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while hashing API key: {}", e.getMessage(), e);
            throw CommonExceptions.operationFailed("Hashing error: " + e.getMessage());
        }
    }
}
