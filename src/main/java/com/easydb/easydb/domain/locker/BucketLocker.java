package com.easydb.easydb.domain.locker;

import com.easydb.easydb.domain.bucket.BucketName;
import java.time.Duration;

public interface BucketLocker {
    void lockBucket(BucketName bucketName);
    void lockBucket(BucketName bucketName, Duration timeout);
    void unlockBucket(BucketName bucketName);
}
