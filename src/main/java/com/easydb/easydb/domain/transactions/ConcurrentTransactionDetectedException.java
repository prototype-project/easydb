package com.easydb.easydb.domain.transactions;

public class ConcurrentTransactionDetectedException extends RuntimeException {
    public ConcurrentTransactionDetectedException(String msg) {
        super(msg);
    }
}
