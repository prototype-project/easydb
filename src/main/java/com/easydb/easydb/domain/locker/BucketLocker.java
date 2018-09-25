package com.easydb.easydb.domain.locker;

import java.time.Duration;

public interface BucketLocker {
    void lockBucket(String spaceName, String bucketName);
    void lockBucket(String spaceName, String bucketName, Duration timeout);
    void unlockBucket(String spaceName, String bucketName);
}
