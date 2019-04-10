package com.easydb.easydb.api.bucket;

public class ElementIdMustNotBeEmptyException extends RuntimeException {
    public ElementIdMustNotBeEmptyException() {
        super("Element id must not be empty");
    }
}
