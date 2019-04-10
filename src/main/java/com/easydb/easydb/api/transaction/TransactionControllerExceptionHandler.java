package com.easydb.easydb.api.transaction;

import com.easydb.easydb.api.ApiError;
import com.easydb.easydb.api.ApiErrorMapper;
import com.easydb.easydb.domain.transactions.TransactionAbortedException;
import com.easydb.easydb.domain.transactions.TransactionDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class TransactionControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {TransactionDoesNotExistException.class})
    ResponseEntity<ApiError> handleTransactionDoesNotExistException(TransactionDoesNotExistException ex) {
        return mapError(ApiError.of("TRANSACTION_DOES_NOT_EXISTS", HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(value = {TransactionAbortedException.class})
    ResponseEntity<ApiError> handleTransactionAbortion(TransactionAbortedException ex) {
        return mapError(ApiError.of("TRANSACTION_ABORTED", HttpStatus.OK, ex.getMessage()));
    }

    private ResponseEntity<ApiError> mapError(ApiError apiError) {
        return ApiErrorMapper.map(apiError);
    }
}
