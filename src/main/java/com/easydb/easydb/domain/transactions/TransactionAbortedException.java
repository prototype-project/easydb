package com.easydb.easydb.domain.transactions;

// TODO handle this exception in api
public class TransactionAbortedException extends RuntimeException {
    TransactionAbortedException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
