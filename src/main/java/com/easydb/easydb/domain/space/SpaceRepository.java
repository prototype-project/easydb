package com.easydb.easydb.domain.space;

public interface SpaceRepository {
    void save(Space toSave) throws SpaceNameNotUniqueException;

    boolean exists(String name);

    Space get(String name) throws SpaceDoesNotExistException;

    void remove(String name) throws SpaceDoesNotExistException;

    void update(Space toUpdate) throws SpaceDoesNotExistException;
}