package com.easydb.easydb.api.bucket;

import com.easydb.easydb.api.ApiError;
import com.easydb.easydb.api.ApiErrorMapper;
import com.easydb.easydb.api.transaction.ElementFieldsMustNotBeNullException;
import com.easydb.easydb.api.transaction.ElementIdMustBeEmptyException;
import com.easydb.easydb.api.transaction.ElementIdMustNotBeEmptyException;
import com.easydb.easydb.domain.bucket.BucketAlreadyExistsException;
import com.easydb.easydb.domain.bucket.BucketDoesNotExistException;
import com.easydb.easydb.domain.bucket.ElementAlreadyExistsException;
import com.easydb.easydb.domain.bucket.ElementDoesNotExistException;
import com.easydb.easydb.domain.bucket.InvalidPaginationDataException;
import com.easydb.easydb.infrastructure.bucket.graphql.QueryValidationException;
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

    @ExceptionHandler(value = {QueryValidationException.class})
    ResponseEntity<ApiError> handleQueryValidationException(QueryValidationException ex) {
        return mapError(ApiError.of("INVALID_QUERY", HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(value = {ElementIdMustBeEmptyException.class})
    ResponseEntity<ApiError> elementIdMustBeEmptyException(ElementIdMustBeEmptyException ex) {
        return mapError(ApiError.of("ELEMENT_ID_MUST_BE_EMPTY", HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(value = {ElementIdMustNotBeEmptyException.class})
    ResponseEntity<ApiError> elementIdMustNotBeEmptyException(ElementIdMustNotBeEmptyException ex) {
        return mapError(ApiError.of("ELEMENT_ID_MUST_NOT_BE_EMPTY", HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(value = {ElementFieldsMustNotBeNullException.class})
    ResponseEntity<ApiError> elementFieldsMustNotBeNullException(ElementFieldsMustNotBeNullException ex) {
        return mapError(ApiError.of("ELEMENT_FIELDS_MUST_NOT_BE_NULL", HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    private ResponseEntity<ApiError> mapError(ApiError apiError) {
        return ApiErrorMapper.map(apiError);
    }
}
