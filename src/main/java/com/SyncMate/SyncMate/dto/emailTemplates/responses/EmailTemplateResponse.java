package com.SyncMate.SyncMate.dto.emailTemplates.responses;

import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.dto.emailTemplates.EmailTemplateDto;


public class EmailTemplateResponse extends MakeResponseDto<EmailTemplateDto> {
    public EmailTemplateResponse(boolean success, String message, EmailTemplateDto data) {
        super(success, message, data);
    }
}
