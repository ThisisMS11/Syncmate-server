package com.SyncMate.SyncMate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    String username;

    @NotBlank
    @Email
    String email;

    @NotNull
    String password;
}
