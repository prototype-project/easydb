package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.BucketDoesNotExistException;
import com.easydb.easydb.domain.bucket.ElementDoesNotExistException;
import com.easydb.easydb.domain.bucket.InvalidPaginationDataException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class BucketControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BucketDoesNotExistException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    void handleBucketDoesNotExistException() {
    }

    @ExceptionHandler(value = {ElementDoesNotExistException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    void handleElementDoesNotExistException() {
    }

    @ExceptionHandler(value = {InvalidPaginationDataException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    void handleInvalidPaginationDataException() {
    }
}
