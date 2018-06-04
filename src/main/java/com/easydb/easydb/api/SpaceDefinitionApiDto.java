package com.easydb.easydb.api;

import com.easydb.easydb.domain.space.Space;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SpaceDefinitionApiDto {

	@JsonProperty("spaceName")
	private final String spaceName;

	@JsonCreator
	SpaceDefinitionApiDto(@JsonProperty("spaceName") String spaceName) {
		this.spaceName = spaceName;
	}

	Space toDomain() {
		return Space.of(spaceName);
	}
}
