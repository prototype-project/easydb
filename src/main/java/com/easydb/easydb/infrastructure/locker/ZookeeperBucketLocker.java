package com.easydb.easydb.infrastructure.locker;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.config.ZookeeperProperties;
import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.LockNotHoldException;
import com.easydb.easydb.domain.locker.LockTimeoutException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

public class ZookeeperBucketLocker implements BucketLocker {

    private final ZookeeperProperties properties;
    private final CuratorFramework client;
    private final ApplicationMetrics metrics;
    private final String spaceName;

    public ZookeeperBucketLocker(String spaceName,
                                 ZookeeperProperties properties,
                                 CuratorFramework client,
                                 ApplicationMetrics metrics) {
        this.spaceName = spaceName;
        this.properties = properties;
        this.client = client;
        this.metrics = metrics;
    }

    private final Map<String, InterProcessSemaphoreMutex> locksMap = new HashMap<>();

    @Override
    public void lockBucket(String bucketName) {
        lockBucket(bucketName, Duration.ofMillis(properties.getLockerTimeoutMillis()));
        metrics.getBucketLockerCounter(spaceName, bucketName).increment();
    }

    @Override
    public void lockBucket(String bucketName, Duration timeout) {
        boolean acquired;
        InterProcessSemaphoreMutex curatorLock = new InterProcessSemaphoreMutex(
                client, buildLockPath(spaceName, bucketName));
        try {
            acquired = curatorLock.acquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            metrics.getLockerErrorCounter(spaceName, bucketName).increment();
            throw new ElementLockerException(e);
        }
        if (!acquired) {
            metrics.getLockerTimeoutsCounter(spaceName, bucketName).increment();
            throw new LockTimeoutException(spaceName, bucketName, timeout);
        } else {
            locksMap.put(bucketName, curatorLock);
        }
    }

    @Override
    public void unlockBucket(String bucketName) {
        InterProcessSemaphoreMutex curatorLock = locksMap.remove(bucketName);
        if (curatorLock == null) {
            metrics.getLockerErrorCounter(spaceName, bucketName).increment();
            throw new LockNotHoldException(spaceName, bucketName);
        }

        try {
            curatorLock.release();
            metrics.getBucketLockerUnlockedCounter(spaceName, bucketName).increment();
        } catch (Exception e) {
            metrics.getLockerErrorCounter(spaceName, bucketName).increment();
            throw new ElementLockerException(e);
        }
    }

    private String buildLockPath(String spaceName, String bucketName) {
        return "/" + spaceName + "/" + bucketName;
    }
}
