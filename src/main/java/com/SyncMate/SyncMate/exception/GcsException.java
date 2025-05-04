package com.SyncMate.SyncMate.exception;

public class GcsException extends ApplicationException {

    public static final String OPERATION_FAILED = "OPERATION_FAILED";

    public GcsException(String errorCode, String message) {
        super(errorCode, message);
    }

    // Helper factory methods with cause
    public static GcsException uploadFailed(String fileName) {
        return new GcsException(OPERATION_FAILED, "Failed to upload file: " + fileName);
    }

    public static GcsException downloadFailed(String fileName) {
        return new GcsException(OPERATION_FAILED, "Failed to download file: " + fileName);
    }

    public static GcsException deleteFailed(String fileName) {
        return new GcsException(OPERATION_FAILED, "Failed to delete file: " + fileName);
    }

    public static GcsException fileNotFound(String fileName) {
        return new GcsException(OPERATION_FAILED, "File not found: " + fileName);
    }
}
