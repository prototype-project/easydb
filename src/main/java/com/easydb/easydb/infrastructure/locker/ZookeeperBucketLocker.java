package com.easydb.easydb.infrastructure.locker;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.config.ZookeeperProperties;
import com.easydb.easydb.domain.BucketName;
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
    public void lockBucket(BucketName bucketName) {
        lockBucket(bucketName, Duration.ofMillis(properties.getLockerTimeoutMillis()));
    }

    @Override
    public void lockBucket(BucketName bucketName, Duration timeout) {
        metrics.bucketLockingTimer(bucketName).record(
                () -> lockWithoutTimer(bucketName, timeout));
    }

    @Override
    public void unlockBucket(BucketName bucketName) {
        metrics.bucketUnLockingTimer(bucketName).record(
                () -> unlockWithoutTimer(bucketName));
    }

    private void lockWithoutTimer(BucketName bucketName, Duration timeout) {
        boolean acquired;

        try {
            acquired = zookeeperLocker.lockOnPath(buildLockPath(bucketName), timeout);
        } catch (Exception e) {
            metrics.bucketLockerErrorCounter(bucketName).increment();
            throw new UnexpectedLockerException(e);
        }
        if (!acquired) {
            metrics.bucketLockerTimeoutsCounter(bucketName).increment();
            throw new LockTimeoutException(bucketName, timeout);
        }
        metrics.bucketLockerCounter(bucketName).increment();
    }


    private void unlockWithoutTimer(BucketName bucketName) {
        try {
            zookeeperLocker.unlockOnPath(buildLockPath(bucketName));
            metrics.bucketLockerUnlockedCounter(bucketName).increment();
        } catch (LockNotHoldException e) {
            metrics.bucketLockerErrorCounter(bucketName).increment();
            throw e;
        } catch (Exception e) {
            metrics.bucketLockerErrorCounter(bucketName).increment();
            throw new UnexpectedLockerException(e);
        }
    }

    private String buildLockPath(BucketName bucketName) {
        return "/" + bucketName.getSpaceName() + "/" + bucketName.getName();
    }
}
