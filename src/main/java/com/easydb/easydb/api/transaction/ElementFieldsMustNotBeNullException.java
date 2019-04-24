package com.easydb.easydb.api.transaction;

class ElementFieldsMustNotBeNullException extends RuntimeException {
    ElementFieldsMustNotBeNullException() {
        super("Element fields must not be null");
    }
}
