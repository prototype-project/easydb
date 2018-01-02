package com.easydb.easydb.domain.space;

public class SpaceDefinitionQueryDto {
    private final String spaceName;

    private SpaceDefinitionQueryDto(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public static SpaceDefinitionQueryDto of(String spaceName) {
        return new SpaceDefinitionQueryDto(spaceName);
    }

    public static SpaceDefinitionQueryDto of(SpaceDefinition spaceDefinition) {
        return SpaceDefinitionQueryDto.of(spaceDefinition.getSpaceName());
    }
}
