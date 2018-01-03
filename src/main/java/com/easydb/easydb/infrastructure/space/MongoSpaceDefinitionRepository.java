package com.easydb.easydb.infrastructure.space;

import com.easydb.easydb.domain.space.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

public class MongoSpaceDefinitionRepository implements SpaceDefinitionRepository {
    private final String SPACE_COLLECTION_NAME = "__SPACES";
    private final MongoTemplate mongoTemplate;

    public MongoSpaceDefinitionRepository(
            MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public SpaceDefinition save(SpaceDefinitionCreateDto toSave) {
        if (exists(toSave.getSpaceName())) {
            throw new SpaceNameNotUniqueException();
        }
        mongoTemplate.insert(PersistentSpaceDefinition.of(toSave), SPACE_COLLECTION_NAME);
        return SpaceDefinition.of(toSave.getSpaceName());
    }

    @Override
    public boolean exists(String spaceName) {
        try {
            get(spaceName);
            return true;
        } catch (SpaceDoesNotExist e) {
            return false;
        }
    }

    @Override
    public SpaceDefinitionQueryDto get(String spaceName) throws SpaceDoesNotExist{
        PersistentSpaceDefinition persistentSpaceDefinition = getPersistentElement(spaceName);
        return Optional.ofNullable(persistentSpaceDefinition)
                .map(it -> SpaceDefinitionQueryDto.of(it.toDomain()))
                .orElseThrow(() -> new SpaceDoesNotExist(spaceName));
    }

    private PersistentSpaceDefinition getPersistentElement(String spaceName) {
        return mongoTemplate.findById(spaceName, PersistentSpaceDefinition.class, SPACE_COLLECTION_NAME);
    }
}
