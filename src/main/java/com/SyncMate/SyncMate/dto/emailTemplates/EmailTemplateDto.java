package com.SyncMate.SyncMate.dto.emailTemplates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailTemplateDto {
    private Long id;

    private String name;

    private String subject;

    private String body;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
