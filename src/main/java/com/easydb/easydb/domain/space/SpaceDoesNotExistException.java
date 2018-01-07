package com.easydb.easydb.domain.space;

public class SpaceDoesNotExistException extends RuntimeException {
    public SpaceDoesNotExistException(String spaceName) {
        super("Space " + spaceName + " does not exist.");
    }
}
