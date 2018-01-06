package com.easydb.easydb.domain.space.dto;

import com.easydb.easydb.domain.space.SpaceDefinition;

public class SpaceDefinitionDto {
    private final String spaceName;

    private SpaceDefinitionDto(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public static SpaceDefinitionDto of(String spaceName) {
        return new SpaceDefinitionDto(spaceName);
    }

    public static SpaceDefinitionDto of(SpaceDefinition spaceDefinition) {
        return SpaceDefinitionDto.of(spaceDefinition.getSpaceName());
    }
}
