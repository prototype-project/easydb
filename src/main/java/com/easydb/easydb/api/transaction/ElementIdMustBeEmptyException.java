package com.easydb.easydb.api.transaction;

public class ElementIdMustBeEmptyException extends RuntimeException {
    ElementIdMustBeEmptyException() {
        super("Element id must be empty during create operation");
    }
}
