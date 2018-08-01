package com.easydb.easydb.domain.transactions;

public class TransactionAbortedException extends RuntimeException {
    public TransactionAbortedException(String msg) {
        super(msg);
    }
}
