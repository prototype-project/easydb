package com.easydb.easydb.domain.transactions;

public class TransactionAbortedException extends RuntimeException {
    TransactionAbortedException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
