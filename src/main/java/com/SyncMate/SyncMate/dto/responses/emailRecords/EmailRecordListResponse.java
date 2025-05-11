package com.SyncMate.SyncMate.dto.responses.emailRecords;

import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.entity.EmailRecord;

import java.util.List;

public class EmailRecordListResponse extends MakeResponseDto<List<EmailRecord>> {

    public EmailRecordListResponse(boolean success, String message, List<EmailRecord> data) {
        super(success, message, data);
    }
}
