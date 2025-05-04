package com.SyncMate.SyncMate.dto.email;
import com.SyncMate.SyncMate.enums.EmailStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {
    private Long id;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Body is required")
    private String body;

    @NotNull(message = "Scheduled time is required")
    private Long scheduledTime;

    private EmailStatus status = EmailStatus.PENDING;

    @NotNull(message = "Contact ID is required")
    private Long contactId;

    private List<Long> attachmentIds;
}
