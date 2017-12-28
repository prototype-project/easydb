package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.BucketOrElementDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SpaceControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BucketOrElementDoesNotExistException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    void handleBucketOrElementDoesNotExistException() {
    }
}
