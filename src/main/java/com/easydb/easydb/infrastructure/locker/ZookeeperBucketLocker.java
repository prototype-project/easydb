package com.easydb.easydb.infrastructure.locker;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.config.ZookeeperProperties;
import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.LockNotHoldException;
import com.easydb.easydb.domain.locker.LockTimeoutException;
import java.time.Duration;
import org.apache.curator.framework.CuratorFramework;

public class ZookeeperBucketLocker implements BucketLocker {

    private final ZookeeperProperties properties;
    private final ApplicationMetrics metrics;
    private final ZookeeperLocker zookeeperLocker;

    public ZookeeperBucketLocker(CuratorFramework client,
                                 ZookeeperProperties properties,
                                 ApplicationMetrics metrics) {
        this.properties = properties;
        this.metrics = metrics;
        this.zookeeperLocker = new ZookeeperLocker(client, metrics);
    }

    @Override
    public void lockBucket(String spaceName, String bucketName) {
        lockBucket(spaceName, bucketName, Duration.ofMillis(properties.getLockerTimeoutMillis()));
    }

    @Override
    public void lockBucket(String spaceName, String bucketName, Duration timeout) {
        boolean acquired;

        try {
            acquired = zookeeperLocker.lockOnPath(buildLockPath(spaceName, bucketName), timeout);
        } catch (Exception e) {
            metrics.getBucketLockerErrorCounter(spaceName, bucketName).increment();
            throw new UnexpectedLockerException(e);
        }
        if (!acquired) {
            metrics.getBucketLockerTimeoutsCounter(spaceName, bucketName).increment();
            throw new LockTimeoutException(spaceName, bucketName, timeout);
        }
        metrics.getBucketLockerCounter(spaceName, bucketName).increment();
    }

    @Override
    public void unlockBucket(String spaceName, String bucketName) {
        try {
            zookeeperLocker.unlockOnPath(buildLockPath(spaceName, bucketName));
            metrics.getBucketLockerUnlockedCounter(spaceName, bucketName).increment();
        } catch (LockNotHoldException e) {
            metrics.getBucketLockerErrorCounter(spaceName, bucketName).increment();
            throw e;
        } catch (Exception e) {
            metrics.getBucketLockerErrorCounter(spaceName, bucketName).increment();
            throw new UnexpectedLockerException(e);
        }
    }

    private String buildLockPath(String spaceName, String bucketName) {
        return "/" + spaceName + "/" + bucketName;
    }
}
