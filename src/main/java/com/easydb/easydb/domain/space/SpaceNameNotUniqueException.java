package com.easydb.easydb.domain.space;

public class SpaceNameNotUniqueException extends RuntimeException {
    public SpaceNameNotUniqueException() {
        super("Space name must be unique.");
    }
}
