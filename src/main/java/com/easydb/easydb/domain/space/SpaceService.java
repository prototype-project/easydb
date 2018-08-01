package com.easydb.easydb.domain.space;

import com.easydb.easydb.domain.bucket.BucketRepository;

public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final BucketRepository bucketRepository;

    public SpaceService(SpaceRepository spaceRepository, BucketRepository bucketRepository) {
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
    }

    public void remove(String name) {
        Space space = spaceRepository.get(name);
        space.getBuckets().stream()
                .map(bucket -> name + ":" + bucket)
                .forEach(bucketRepository::removeBucket);
        spaceRepository.remove(name);
    }
}
