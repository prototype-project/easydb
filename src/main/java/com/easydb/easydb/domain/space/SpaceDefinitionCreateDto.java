package com.easydb.easydb.domain.space;

public class SpaceDefinitionCreateDto {
    private final String spaceName;

    private SpaceDefinitionCreateDto(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public static SpaceDefinitionCreateDto of(String spaceName) {
        return new SpaceDefinitionCreateDto(spaceName);
    }
}