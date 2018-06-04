package com.easydb.easydb.infrastructure.space;

import com.easydb.easydb.domain.space.Space;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
class PersistentSpace {

    @Id
    private final String spaceName;

    private PersistentSpace(String spaceName) {
        this.spaceName = spaceName;
    }

    Space toDomain() {
        return Space.of(spaceName);
    }

    static PersistentSpace of(Space spaceDefinition) {
        return new PersistentSpace(spaceDefinition.getName());
    }
}
