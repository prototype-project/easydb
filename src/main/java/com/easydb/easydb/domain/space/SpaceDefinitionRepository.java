package com.easydb.easydb.domain.space;

public interface SpaceDefinitionRepository {
    SpaceDefinitionQueryDto save(SpaceDefinitionCreateDto toSave) throws SpaceNameNotUniqueException;

    boolean exists(String spaceName);

    SpaceDefinitionQueryDto get(String spaceName) throws SpaceDoesNotExist;

    void remove(String spaceName);
}