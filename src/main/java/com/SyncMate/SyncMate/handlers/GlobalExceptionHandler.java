package com.SyncMate.SyncMate.handlers;
import com.SyncMate.SyncMate.exception.ApiError;
import com.SyncMate.SyncMate.exception.ApplicationException;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiError> handleUserException(
            UserException ex) {

        if (ex.getErrorCode().equals(UserException.USER_EXISTS)) {
            log.warn("Registration attempt failed: {}", ex.getMessage());
        }

        HttpStatus status = mapErrorCodeToStatus(ex.getErrorCode());
        ApiError error = buildApiError(status, ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(error, status);
    }

    // Generic handler for other application exceptions
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiError> handleApplicationException(
            ApplicationException ex) {

        log.error("Application exception: {}", ex.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError error = buildApiError(status, ex.getMessage(), ex.getErrorCode());

        return new ResponseEntity<>(error, status);
    }

    private HttpStatus mapErrorCodeToStatus(String errorCode) {
        return switch(errorCode) {
            case UserException.USER_EXISTS -> HttpStatus.CONFLICT;
            case UserException.INVALID_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
            case CommonExceptions.RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CommonExceptions.INVALID_REQUEST -> HttpStatus.BAD_REQUEST;
            case CommonExceptions.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case CommonExceptions.FORBIDDEN -> HttpStatus.FORBIDDEN;
            case CommonExceptions.OPERATION_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR;
            case CommonExceptions.INPUT_FIELD_VALUE_ALREADY_EXISTS -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };
    }

    private ApiError buildApiError(HttpStatus status, String message, String errorCode) {
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(message);
        error.setErrorCode(errorCode);
        return error;
    }
}
