package com.SyncMate.SyncMate.dto.emailTemplates.responses;

import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.dto.emailTemplates.EmailTemplateDto;

import java.util.List;

public class EmailTemplatesListResponse extends MakeResponseDto<List<EmailTemplateDto>> {
    public EmailTemplatesListResponse(boolean success, String message, List<EmailTemplateDto> data) {
        super(success, message, data);
    }
}
