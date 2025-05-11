package com.SyncMate.SyncMate.dto.responses.authentication;

import com.SyncMate.SyncMate.dto.TokenResponse;
import com.SyncMate.SyncMate.dto.common.MakeResponseDto;

public class AuthResponse extends MakeResponseDto<TokenResponse> {
    public AuthResponse(boolean success, String message, TokenResponse data) {
        super(success, message, data);
    }
}
