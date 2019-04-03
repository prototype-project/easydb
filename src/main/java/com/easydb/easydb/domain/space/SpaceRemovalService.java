package com.easydb.easydb.domain.space;

import com.easydb.easydb.domain.bucket.NamesResolver;
import com.easydb.easydb.domain.bucket.transactions.BucketRepository;
import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.transactions.Retryer;

public class SpaceRemovalService {

    private final SpaceRepository spaceRepository;
    private final BucketRepository bucketRepository;
    private final SpaceLocker spaceLocker;
    private final BucketLocker bucketLocker;
    private final Retryer lockerRetryer;

    public SpaceRemovalService(SpaceRepository spaceRepository, BucketRepository bucketRepository,
                               SpaceLocker spaceLocker, BucketLocker bucketLocker, Retryer lockerRetryer) {
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
        this.spaceLocker = spaceLocker;
        this.bucketLocker = bucketLocker;
        this.lockerRetryer = lockerRetryer;
    }

    public void remove(String spaceName) {
        lockerRetryer.performWithRetries(() -> spaceLocker.lockSpace(spaceName));
        try {
            Space space = spaceRepository.get(spaceName);
            space.getBuckets().stream()
                    .map(bucket -> NamesResolver.resolve(spaceName, bucket))
                    .forEach(bucketName -> removeBucket(spaceName, bucketName));
            spaceRepository.remove(spaceName);
        } finally {
            spaceLocker.unlockSpace(spaceName);
        }
    }

    private void removeBucket(String spaceName, String bucketName) {
        lockerRetryer.performWithRetries(() -> bucketLocker.lockBucket(spaceName, bucketName));
        try {
            bucketRepository.removeBucket(bucketName);
        } finally {
            bucketLocker.unlockBucket(spaceName, bucketName);
        }
    }
}
