package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.BucketAlreadyExistsException;
import com.easydb.easydb.domain.bucket.BucketDoesNotExistException;
import com.easydb.easydb.domain.bucket.ElementAlreadyExistsException;
import com.easydb.easydb.domain.bucket.ElementDoesNotExistException;
import com.easydb.easydb.domain.bucket.InvalidPaginationDataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class BucketControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BucketDoesNotExistException.class})
    ResponseEntity<ApiError> handleBucketDoesNotExistException(BucketDoesNotExistException ex) {
        return mapError(ApiError.of("BUCKET_DOES_NOT_EXIST", HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(value = {BucketAlreadyExistsException.class})
    ResponseEntity<ApiError> handleBucketAlreadyExistsException(BucketAlreadyExistsException ex) {
        return mapError(ApiError.of("BUCKET_ALREADY_EXISTS", HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(value = {ElementDoesNotExistException.class})
    ResponseEntity<ApiError> handleElementDoesNotExistException(ElementDoesNotExistException ex) {
        return mapError(ApiError.of("ELEMENT_DOES_NOT_EXIST", HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(value = {InvalidPaginationDataException.class})
    ResponseEntity<ApiError> handleInvalidPaginationDataException(InvalidPaginationDataException ex) {
        return mapError(ApiError.of("INVALID_PAGINATION_DATA", HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(value = {ElementAlreadyExistsException.class})
    ResponseEntity<ApiError> handleElementExistsException(ElementAlreadyExistsException ex) {
        return mapError(ApiError.of("ELEMENT_ALREADY_EXISTS", HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    private ResponseEntity<ApiError> mapError(ApiError apiError) {
        return ApiErrorMapper.map(apiError);
    }
}
