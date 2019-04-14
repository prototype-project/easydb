package com.easydb.easydb.domain.locker;

import com.easydb.easydb.domain.BucketName;
import java.time.Duration;

public interface ElementsLocker {
    void lockElement(BucketName bucketName, String elementId);
    void lockElement(BucketName bucketName, String elementId, Duration timeout);
    void unlockElement(BucketName bucketName, String elementId);
}