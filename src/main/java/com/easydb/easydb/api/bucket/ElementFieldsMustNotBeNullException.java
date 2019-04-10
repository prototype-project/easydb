package com.easydb.easydb.api.bucket;

public class ElementFieldsMustNotBeNullException extends RuntimeException {
    public ElementFieldsMustNotBeNullException() {
        super("Element fields must not be null");
    }
}
