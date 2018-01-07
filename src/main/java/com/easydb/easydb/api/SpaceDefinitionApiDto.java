package com.easydb.easydb.api;

import com.easydb.easydb.domain.space.SpaceDefinition;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class SpaceDefinitionApiDto {
	@JsonProperty("spaceName")
	private final String spaceName;

	@JsonCreator
	SpaceDefinitionApiDto(@JsonProperty("spaceName") String spaceName) {
		this.spaceName = spaceName;
	}

	SpaceDefinition toDomain() {
		return SpaceDefinition.of(spaceName);
	}
}
