package com.SyncMate.SyncMate.dto.emailTemplates;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestEmailTemplate {

    @NotBlank(message = "Template name is required")
    @Size(min = 1, max = 100, message = "Template name must be between 1 and 100 characters")
    private String name;

    @NotBlank(message = "Subject is required")
    @Size(min = 1, max = 10000, message = "Subject must be between 1 and 10000 characters")
    private String subject;

    @NotBlank(message = "Body is required")
    @Size(min = 1, max = 100, message = "Body must be between 1 and 10000 characters")
    private String body;
}
