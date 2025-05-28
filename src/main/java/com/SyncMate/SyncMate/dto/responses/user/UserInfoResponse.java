package com.SyncMate.SyncMate.dto.responses.user;

import com.SyncMate.SyncMate.dto.UserinfoDto;
import com.SyncMate.SyncMate.dto.common.MakeResponseDto;

public class UserInfoResponse extends MakeResponseDto<UserinfoDto> {
    public UserInfoResponse(boolean success, String message, UserinfoDto data) {
        super(success, message, data);
    }
}


