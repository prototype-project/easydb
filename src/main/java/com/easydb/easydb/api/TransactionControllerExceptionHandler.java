package com.easydb.easydb.api;

import com.easydb.easydb.domain.transactions.TransactionDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class TransactionControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {TransactionDoesNotExistException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    void handleTransactionDoesNotExistException() {
    }

    @ExceptionHandler(value = {ElementIdMustNotBeEmptyException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    void handleElementIdMustNotBeEmptyException() {
    }

    @ExceptionHandler(value = {ElementIdMustBeEmptyException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    void handleElementIdMustBeEmptyException() {
    }
}
