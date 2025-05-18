package com.SyncMate.SyncMate.dto.APIKey;

import com.SyncMate.SyncMate.enums.PartnerLevel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class APIKeyResponseDto {
    private Long id;
    private String name;
    private String description;
    private String apiKeyHash;
    private PartnerLevel level;
    private Long expiryTimestamp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
