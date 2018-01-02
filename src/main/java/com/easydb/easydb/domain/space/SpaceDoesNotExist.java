package com.easydb.easydb.domain.space;

public class SpaceDoesNotExist extends RuntimeException {
    public SpaceDoesNotExist(String spaceName) {
        super("Space " + spaceName + " does not exist.");
    }
}
