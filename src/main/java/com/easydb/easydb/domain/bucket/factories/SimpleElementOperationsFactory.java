package com.easydb.easydb.domain.bucket.factories;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.SimpleElementOperations;
import com.easydb.easydb.domain.space.SpaceRepository;

public class SimpleElementOperationsFactory {
    private final SpaceRepository spaceRepository;
    private final BucketRepository bucketRepository;

    public SimpleElementOperationsFactory(SpaceRepository spaceRepository,
                                   BucketRepository bucketRepository) {
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
    }

    public SimpleElementOperations buildSimpleElementOperations(String spaceName) {
        return new SimpleElementOperations(spaceName, spaceRepository, bucketRepository);
    }
}
