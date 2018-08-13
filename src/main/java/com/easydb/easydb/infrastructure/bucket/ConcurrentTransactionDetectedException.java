package com.easydb.easydb.infrastructure.bucket;

public class ConcurrentTransactionDetectedException extends RuntimeException {
    public ConcurrentTransactionDetectedException(String msg) {
        super(msg);
    }
}
