package com.easydb.easydb.infrastructure.space;

import com.easydb.easydb.domain.space.SpaceDefinition;
import com.easydb.easydb.domain.space.SpaceDefinitionRepository;
import com.easydb.easydb.domain.space.SpaceDoesNotExist;
import com.easydb.easydb.domain.space.SpaceNameNotUniqueException;
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
    public void save(SpaceDefinition toSave) {
        if (exists(toSave.getSpaceName())) {
            throw new SpaceNameNotUniqueException();
        }
        mongoTemplate.insert(PersistentSpaceDefinition.of(toSave), SPACE_COLLECTION_NAME);
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
    public SpaceDefinition get(String spaceName) throws SpaceDoesNotExist{
        PersistentSpaceDefinition persistentSpaceDefinition = getPersistentElement(spaceName);
        return Optional.ofNullable(persistentSpaceDefinition)
                .map(PersistentSpaceDefinition::toDomain)
                .orElseThrow(() -> new SpaceDoesNotExist(spaceName));
    }

    @Override
    public void remove(String spaceName) {
        mongoTemplate.remove(getPersistentElement(spaceName), SPACE_COLLECTION_NAME);
    }

    private PersistentSpaceDefinition getPersistentElement(String spaceName) {
        return mongoTemplate.findById(spaceName, PersistentSpaceDefinition.class, SPACE_COLLECTION_NAME);
    }
}
