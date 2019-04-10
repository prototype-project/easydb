package com.easydb.easydb.api.bucket;

public class ElementIdMustBeEmptyException extends RuntimeException {
    public ElementIdMustBeEmptyException() {
        super("Element id must be empty during create operation");
    }
}
