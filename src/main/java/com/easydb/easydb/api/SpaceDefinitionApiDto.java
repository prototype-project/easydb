package com.easydb.easydb.api;

import com.easydb.easydb.domain.space.Space;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class SpaceDefinitionApiDto {

    @NotEmpty
    @JsonProperty("spaceName")
    private final String spaceName;

    @JsonCreator
    public SpaceDefinitionApiDto(@JsonProperty("spaceName") String spaceName) {
        this.spaceName = spaceName;
    }

    Space toDomain() {
        return Space.of(spaceName);
    }
}
