package com.easydb.easydb.infrastructure.locker;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.config.ZookeeperProperties;
import com.easydb.easydb.domain.locker.LockNotHoldException;
import com.easydb.easydb.domain.locker.LockTimeoutException;
import com.easydb.easydb.domain.locker.SpaceLocker;
import java.time.Duration;
import org.apache.curator.framework.CuratorFramework;

public class ZookeeperSpaceLocker implements SpaceLocker {

    private final ZookeeperProperties properties;
    private final ApplicationMetrics metrics;
    private final ZookeeperLocker zookeeperLocker;

    public ZookeeperSpaceLocker(CuratorFramework client,
                                ZookeeperProperties properties,
                                ApplicationMetrics metrics) {
        this.properties = properties;
        this.metrics = metrics;
        this.zookeeperLocker = new ZookeeperLocker(client);
    }

    @Override
    public void lockSpace(String spaceName) {
        lockSpace(spaceName, Duration.ofMillis(properties.getLockerTimeoutMillis()));
    }

    @Override
    public void lockSpace(String spaceName, Duration timeout) {
        metrics.spaceLockingTimer(spaceName).record(
                () -> lockWithoutTimer(spaceName, timeout));
    }

    @Override
    public void unlockSpace(String spaceName) {
        metrics.spaceUnlockingTimer(spaceName).record(
                () -> unlockWithoutTimer(spaceName));
    }

    private void lockWithoutTimer(String spaceName, Duration timeout) {
        boolean acquired;

        try {
            acquired = zookeeperLocker.lockOnPath(buildLockPath(spaceName), timeout);
        } catch (Exception e) {
            metrics.spaceLockerErrorCounter(spaceName).increment();
            throw new UnexpectedLockerException(e);
        }
        if (!acquired) {
            metrics.spaceLockerTimeoutsCounter(spaceName).increment();
            throw new LockTimeoutException(spaceName, timeout);
        }
        metrics.spaceLockerCounter(spaceName).increment();
    }

    private void unlockWithoutTimer(String spaceName) {
        try {
            zookeeperLocker.unlockOnPath(buildLockPath(spaceName));
            metrics.spaceLockerUnlockedCounter(spaceName).increment();
        } catch (LockNotHoldException e) {
            metrics.spaceLockerErrorCounter(spaceName).increment();
            throw e;
        } catch (Exception e) {
            metrics.spaceLockerErrorCounter(spaceName).increment();
            throw new UnexpectedLockerException(e);
        }
    }

    private String buildLockPath(String spaceName) {
        return "/" + spaceName;
    }
}
