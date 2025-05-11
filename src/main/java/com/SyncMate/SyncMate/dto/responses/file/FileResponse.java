package com.SyncMate.SyncMate.dto.responses.file;

import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.entity.File;

public class FileResponse extends MakeResponseDto<File> {
    public FileResponse(boolean success, String message, File data) {
        super(success, message, data);
    }
}
