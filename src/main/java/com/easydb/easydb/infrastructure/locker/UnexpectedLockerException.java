package com.easydb.easydb.infrastructure.locker;

class UnexpectedLockerException extends RuntimeException {
    UnexpectedLockerException(Throwable cause) {
        super(cause);
    }
}
