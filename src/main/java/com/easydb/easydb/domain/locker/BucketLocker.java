package com.easydb.easydb.domain.locker;

import java.time.Duration;

public interface BucketLocker {
    void lockBucket(String bucketName);
    void lockBucket(String bucketName, Duration timeout);
    void unlockBucket(String bucketName);
}
