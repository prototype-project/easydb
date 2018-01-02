package com.easydb.easydb.domain.space;

public class SpaceDefinition {
    private final String spaceName;

    private SpaceDefinition(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public static SpaceDefinition of(String spaceName) {
        return new SpaceDefinition(spaceName);
    }
}
