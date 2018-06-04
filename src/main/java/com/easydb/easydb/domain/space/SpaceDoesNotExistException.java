package com.easydb.easydb.domain.space;

public class SpaceDoesNotExistException extends RuntimeException {
    public SpaceDoesNotExistException(String name) {
        super("Space " + name + " does not exist.");
    }
}
