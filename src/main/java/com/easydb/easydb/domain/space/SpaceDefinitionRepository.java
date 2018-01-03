package com.easydb.easydb.domain.space;

public interface SpaceDefinitionRepository {
    SpaceDefinition save(SpaceDefinitionCreateDto toSave) throws SpaceNameNotUniqueException;

    boolean exists(String spaceName);

    SpaceDefinitionQueryDto get(String spaceName) throws SpaceDoesNotExist;
}