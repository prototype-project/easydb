package com.easydb.easydb.domain.space;

public interface SpaceFactory {
    Space buildSpace(SpaceDefinitionQueryDto spaceDefinition);
}