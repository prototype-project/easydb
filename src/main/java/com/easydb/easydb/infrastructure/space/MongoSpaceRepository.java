package com.easydb.easydb.infrastructure.space;

import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.SpaceDoesNotExistException;
import com.easydb.easydb.domain.space.SpaceNameNotUniqueException;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

public class MongoSpaceRepository implements SpaceRepository {
    private final String SPACE_COLLECTION_NAME = "__SPACES";
    private final MongoTemplate mongoTemplate;

    public MongoSpaceRepository(
            MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(Space toSave) {
        if (exists(toSave.getName())) {
            throw new SpaceNameNotUniqueException();
        }
        mongoTemplate.insert(PersistentSpace.of(toSave), SPACE_COLLECTION_NAME);
    }

    @Override
    public boolean exists(String spaceName) {
        try {
            get(spaceName);
            return true;
        } catch (SpaceDoesNotExistException e) {
            return false;
        }
    }

    @Override
    public Space get(String spaceName) throws SpaceDoesNotExistException {
        PersistentSpace persistentSpace = getPersistentElement(spaceName);
        return Optional.ofNullable(persistentSpace)
                .map(PersistentSpace::toDomain)
                .orElseThrow(() -> new SpaceDoesNotExistException(spaceName));
    }

    @Override
    public void remove(String spaceName) {
        ensureSpaceExists(spaceName);

        mongoTemplate.remove(getPersistentElement(spaceName), SPACE_COLLECTION_NAME);
    }

    private PersistentSpace getPersistentElement(String spaceName) {
        return mongoTemplate.findById(spaceName, PersistentSpace.class, SPACE_COLLECTION_NAME);
    }

    private void ensureSpaceExists(String spaceName) {
        if (!exists(spaceName)) {
            throw new SpaceDoesNotExistException(spaceName);
        }
    }
}
