package com.easydb.easydb.domain.space;

import com.easydb.easydb.domain.bucket.NamesResolver;
import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.transactions.Retryier;

public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final BucketRepository bucketRepository;
    private final SpaceLocker spaceLocker;
    private final BucketLocker bucketLocker;
    private final Retryier lockerRetryier;

    public SpaceService(SpaceRepository spaceRepository, BucketRepository bucketRepository,
                        SpaceLocker spaceLocker, BucketLocker bucketLocker, Retryier lockerRetryier) {
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
        this.spaceLocker = spaceLocker;
        this.bucketLocker = bucketLocker;
        this.lockerRetryier = lockerRetryier;
    }

    public void remove(String name) {
        lockerRetryier.performWithRetries(() -> spaceLocker.lockSpace(name));
        try {
            Space space = spaceRepository.get(name);
            space.getBuckets().stream()
                    .map(bucket -> NamesResolver.resolve(name, bucket))
                    .forEach(bucketName -> removeBucket(name, bucketName));
            spaceRepository.remove(name);
        } finally {
            spaceLocker.unlockSpace(name);
        }
    }

    private void removeBucket(String name, String bucketName) {
        lockerRetryier.performWithRetries(() -> bucketLocker.lockBucket(name, bucketName));
        try {
            bucketRepository.removeBucket(bucketName);
        } finally {
            bucketLocker.unlockBucket(name, bucketName);
        }
    }
}
