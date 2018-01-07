package com.easydb.easydb.domain.space;

public interface SpaceDefinitionRepository {
    void save(SpaceDefinition toSave) throws SpaceNameNotUniqueException;

    boolean exists(String spaceName);

    SpaceDefinition get(String spaceName) throws SpaceDoesNotExistException;

    void remove(String spaceName);
}