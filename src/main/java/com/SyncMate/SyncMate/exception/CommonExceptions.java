package com.SyncMate.SyncMate.exception;

public class CommonExceptions extends ApplicationException {

    // Error code variables
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String INVALID_REQUEST = "INVALID_REQUEST";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String OPERATION_FAILED = "OPERATION_FAILED";
    public static final String INPUT_FIELD_VALUE_ALREADY_EXISTS = "INPUT_FIELD_VALUE_ALREADY_EXISTS";

    public CommonExceptions(String errorCode, String message) {
        super(errorCode, message);
    }

    // Helper factory methods

    public static CommonExceptions resourceNotFound(String identifier) {
        return new CommonExceptions(RESOURCE_NOT_FOUND, "Resource with identifier " + identifier + " not found");
    }

    public static CommonExceptions inputFieldValueAlreadyExists(String identifier) {
        return new CommonExceptions(INPUT_FIELD_VALUE_ALREADY_EXISTS, "Field with value " + identifier + " already exists");
    }

    public static CommonExceptions invalidRequest(String reason) {
        return new CommonExceptions(INVALID_REQUEST, "The request is invalid: " + reason);
    }

    public static CommonExceptions unauthorizedAccess() {
        return new CommonExceptions(UNAUTHORIZED, "You do not have permission to access this resource");
    }

    public static CommonExceptions forbiddenAccess() {
        return new CommonExceptions(FORBIDDEN, "Access to the requested resource is forbidden");
    }

    public static CommonExceptions operationFailed(String operation) {
        return new CommonExceptions(OPERATION_FAILED, "Operation " + operation + " failed due to an internal error");
    }
}
