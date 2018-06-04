package com.easydb.easydb.domain.space;


import com.easydb.easydb.domain.bucket.BucketRepository;

public class SpaceServiceFactory {
    private final BucketRepository bucketRepository;

    public SpaceServiceFactory(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    public SpaceService buildSpaceService(Space spaceDefinition) {
        return new SpaceService(
                spaceDefinition.getName(),
                bucketRepository);
    }
}