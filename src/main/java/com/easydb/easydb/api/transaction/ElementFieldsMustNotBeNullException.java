package com.easydb.easydb.api.transaction;

public class ElementFieldsMustNotBeNullException extends RuntimeException {
    ElementFieldsMustNotBeNullException() {
        super("Element fields must not be null");
    }
}
