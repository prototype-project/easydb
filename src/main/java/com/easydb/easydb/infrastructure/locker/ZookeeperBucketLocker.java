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
        this.zookeeperLocker = new ZookeeperLocker(client);
    }

    @Override
    public void lockBucket(String spaceName, String bucketName) {
        lockBucket(spaceName, bucketName, Duration.ofMillis(properties.getLockerTimeoutMillis()));
    }

    @Override
    public void lockBucket(String spaceName, String bucketName, Duration timeout) {
        metrics.bucketLockingTimer(spaceName, bucketName).record(
                () -> lockWithoutTimer(spaceName, bucketName, timeout));
    }

    @Override
    public void unlockBucket(String spaceName, String bucketName) {
        metrics.bucketUnLockingTimer(spaceName, bucketName).record(
                () -> unlockWithoutTimer(spaceName, bucketName));
    }

    private void lockWithoutTimer(String spaceName, String bucketName, Duration timeout) {
        boolean acquired;

        try {
            acquired = zookeeperLocker.lockOnPath(buildLockPath(spaceName, bucketName), timeout);
        } catch (Exception e) {
            metrics.bucketLockerErrorCounter(spaceName, bucketName).increment();
            throw new UnexpectedLockerException(e);
        }
        if (!acquired) {
            metrics.bucketLockerTimeoutsCounter(spaceName, bucketName).increment();
            throw new LockTimeoutException(spaceName, bucketName, timeout);
        }
        metrics.bucketLockerCounter(spaceName, bucketName).increment();
    }


    private void unlockWithoutTimer(String spaceName, String bucketName) {
        try {
            zookeeperLocker.unlockOnPath(buildLockPath(spaceName, bucketName));
            metrics.bucketLockerUnlockedCounter(spaceName, bucketName).increment();
        } catch (LockNotHoldException e) {
            metrics.bucketLockerErrorCounter(spaceName, bucketName).increment();
            throw e;
        } catch (Exception e) {
            metrics.bucketLockerErrorCounter(spaceName, bucketName).increment();
            throw new UnexpectedLockerException(e);
        }
    }

    private String buildLockPath(String spaceName, String bucketName) {
        return "/" + spaceName + "/" + bucketName;
    }
}
