package com.easydb.easydb.infrastructure.space;

import com.easydb.easydb.domain.space.SpaceDefinition;
import com.easydb.easydb.domain.space.SpaceDefinitionCreateDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
class PersistentSpaceDefinition {

    @Id
    private final String spaceName;

    private PersistentSpaceDefinition(String spaceName) {
        this.spaceName = spaceName;
    }

    static PersistentSpaceDefinition of(SpaceDefinitionCreateDto spaceDefinition) {
        return new PersistentSpaceDefinition(spaceDefinition.getSpaceName());
    }

    SpaceDefinition toDomain() {
        return SpaceDefinition.of(spaceName);
    }
}
