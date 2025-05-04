package com.SyncMate.SyncMate.exception;

public class UserException extends  ApplicationException{
    public static final String USER_EXISTS = "USER_EXISTS";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";

    public UserException(String errorCode, String message) {
        super(errorCode, message);
    }

    // Helper factory methods
    public static UserException userExists(String identifier) {
        return new UserException(USER_EXISTS, "User with identifier " + identifier + " already exists");
    }

    public static UserException invalidCredentials() {
        return new UserException(INVALID_CREDENTIALS,"Invalid username or password");
    }
}
