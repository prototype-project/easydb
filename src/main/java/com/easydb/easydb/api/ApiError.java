package com.easydb.easydb.api;

import org.springframework.http.HttpStatus;

public class ApiError {
    private final String errorCode;
    private final HttpStatus status;
    private final String message;

    private ApiError(String errorCode, HttpStatus status, String message) {
        this.errorCode = errorCode;
        this.status = status;
        this.message = message;
    }

    public static ApiError of(String errorCode, HttpStatus status, String message) {
        return new ApiError(errorCode, status, message);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
