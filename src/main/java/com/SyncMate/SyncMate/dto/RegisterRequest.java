package com.SyncMate.SyncMate.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    String username;
    String email;
    String password;
}
