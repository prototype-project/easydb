package com.easydb.easydb.api.space;

import com.easydb.easydb.api.ApiError;
import com.easydb.easydb.api.ApiErrorMapper;
import com.easydb.easydb.domain.space.SpaceDoesNotExistException;
import com.easydb.easydb.domain.space.SpaceNameNotUniqueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SpaceDefinitionControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {SpaceNameNotUniqueException.class})
    ResponseEntity<ApiError> handleSpaceNameNotUniqueException(SpaceNameNotUniqueException ex) {
        return mapError(ApiError.of("SPACE_NAME_NOT_UNIQUE", HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(value = {SpaceDoesNotExistException.class})
    ResponseEntity<ApiError> handleSpaceDoesNotExistException(SpaceDoesNotExistException ex) {
        return mapError(ApiError.of("SPACE_DOES_NOT_EXIST", HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    private ResponseEntity<ApiError> mapError(ApiError apiError) {
        return ApiErrorMapper.map(apiError);
    }
}
