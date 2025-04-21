package com.SyncMate.SyncMate.exception;

import lombok.Data;

@Data
public abstract class ApplicationException extends RuntimeException {
    private final String errorCode;

    protected ApplicationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}