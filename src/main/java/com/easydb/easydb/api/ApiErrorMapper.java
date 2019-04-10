package com.easydb.easydb.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class ApiErrorMapper {
    public static ResponseEntity<ApiError> map(ApiError apiError) {
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
