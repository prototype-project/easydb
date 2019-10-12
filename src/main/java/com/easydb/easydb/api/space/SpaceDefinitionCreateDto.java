package com.easydb.easydb.api.space;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SpaceDefinitionCreateDto {

    private final String spaceName;

    @JsonCreator
    public SpaceDefinitionCreateDto(@JsonProperty("spaceName") String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceName() {
        return spaceName;
    }
}
