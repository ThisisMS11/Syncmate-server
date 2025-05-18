package com.SyncMate.SyncMate.dto.APIKey;

import com.SyncMate.SyncMate.enums.ExpiryBucket;
import com.SyncMate.SyncMate.enums.PartnerLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class APIKeyRequestDto {
    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Please give a name to your api key")
    private String name;

    private String description;

    @NotNull(message = "API Key level should not be null")
    private PartnerLevel level;

    private ExpiryBucket expiryBucket;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
