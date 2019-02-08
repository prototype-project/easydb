package com.easydb.easydb.domain.bucket.factories;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.ElementService;

public class ElementServiceFactory {
    private final BucketRepository bucketRepository;

    public ElementServiceFactory(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    public ElementService buildElementService(String spaceName) {
        return new ElementService(spaceName, bucketRepository);
    }
}
