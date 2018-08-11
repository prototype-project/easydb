package com.easydb.easydb.api;

class ElementFieldsMustNotBeNull extends RuntimeException {
    ElementFieldsMustNotBeNull() {
        super("Element fields must not be null");
    }
}
