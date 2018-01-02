package com.easydb.easydb.domain.space;

public interface SpaceDefinitionRepository {
    SpaceDefinition save(SpaceDefinitionCreateDto toSave);

    boolean exists(String spaceName);

    SpaceDefinitionQueryDto get(String spaceName) throws SpaceDoesNotExist;
}