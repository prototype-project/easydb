package com.easydb.easydb.api;

import com.easydb.easydb.domain.space.Space;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SpaceDefinitionApiDto {

    private final String spaceName;

    @JsonCreator
    public SpaceDefinitionApiDto(@JsonProperty("spaceName") String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceName() {
        return spaceName;
    }

    Space toDomain() {
        return Space.of(spaceName);
    }
}
