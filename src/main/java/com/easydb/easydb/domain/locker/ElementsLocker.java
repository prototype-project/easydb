package com.easydb.easydb.domain.locker;

import java.time.Duration;

public interface ElementsLocker {
    void lockElement(String bucketName, String elementId);
    void lockElement(String bucketName, String elementId, Duration timeout);
    void unlockElement(String bucketName, String elementId);
}