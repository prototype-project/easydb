package com.easydb.easydb.domain.bucket;

public class ElementAlreadyExistsException extends RuntimeException {
    public ElementAlreadyExistsException(String msg) {
        super(msg);
    }
}
