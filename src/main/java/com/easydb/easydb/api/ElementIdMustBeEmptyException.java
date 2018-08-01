package com.easydb.easydb.api;

class ElementIdMustBeEmptyException extends RuntimeException {
    ElementIdMustBeEmptyException() {
        super("Element id must be empty during create operation");
    }
}
