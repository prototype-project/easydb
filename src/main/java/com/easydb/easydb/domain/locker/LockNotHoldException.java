package com.easydb.easydb.domain.locker;

public class LockNotHoldException extends RuntimeException {
    public LockNotHoldException(String path) {
        super("Unlocking not held lock on path - " + path);
    }
}
