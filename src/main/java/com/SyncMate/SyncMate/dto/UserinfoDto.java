package com.SyncMate.SyncMate.dto;

import com.SyncMate.SyncMate.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserinfoDto {
    private Long id;
    private String username;
    private String email;
    private List<Role> roles;
}
