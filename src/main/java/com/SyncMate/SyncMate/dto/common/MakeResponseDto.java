package com.SyncMate.SyncMate.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MakeResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
}
