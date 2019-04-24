package com.easydb.easydb.api.transaction;

class ElementIdMustNotBeEmptyException extends RuntimeException {
    ElementIdMustNotBeEmptyException() {
        super("Element id must not be empty");
    }
}
