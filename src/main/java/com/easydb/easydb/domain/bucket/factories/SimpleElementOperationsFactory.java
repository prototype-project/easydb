package com.easydb.easydb.domain.bucket.factories;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.SimpleElementOperations;

public class SimpleElementOperationsFactory {
    private final BucketRepository bucketRepository;

    public SimpleElementOperationsFactory(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    public SimpleElementOperations buildSimpleElementOperations(String spaceName) {
        return new SimpleElementOperations(spaceName, bucketRepository);
    }
}
