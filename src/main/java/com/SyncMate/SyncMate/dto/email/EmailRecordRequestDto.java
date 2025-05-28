package com.SyncMate.SyncMate.dto.email;

import com.SyncMate.SyncMate.enums.EmailStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRecordRequestDto {
    private Long id;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Body is required")
    private String body;

    @NotNull(message = "Scheduled time is required")
    private Long scheduledTime;

    private EmailStatus status = EmailStatus.PENDING;

    @NotEmpty(message = "Contact IDs is required")
    private List<Long> contactIds;

    private List<Long> attachmentIds;

    private AdditionalData additionalData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalData {
        private String internshipLink;
        private String resumeLink;
        private String coverLetterLink;
    }
}
