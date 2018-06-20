package com.easydb.easydb.domain.space;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.BucketService;

public class BucketServiceFactory {
    private final BucketRepository bucketRepository;
    private final SpaceRepository spaceRepository;

    public BucketServiceFactory(BucketRepository bucketRepository, SpaceRepository spaceRepository) {
        this.bucketRepository = bucketRepository;
        this.spaceRepository = spaceRepository;
    }

    public BucketService buildBucketService(String spaceName) {
        return new BucketService(
                spaceRepository.get(spaceName),
                bucketRepository);
    }
}