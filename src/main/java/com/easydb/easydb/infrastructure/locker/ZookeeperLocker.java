package com.easydb.easydb.infrastructure.locker;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.config.ZookeeperProperties;
import com.easydb.easydb.domain.locker.ElementsLocker;
import com.easydb.easydb.domain.locker.LockNotHoldException;
import com.easydb.easydb.domain.locker.LockTimeoutException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

public class ZookeeperLocker implements ElementsLocker {

    static class ElementKey {
        private final String bucketName;
        private final String elementId;

        private ElementKey(String bucketName, String elementId) {
            this.bucketName = bucketName;
            this.elementId = elementId;
        }

        public static ElementKey of(String bucketName, String elementId) {
            return new ElementKey(bucketName, elementId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ElementKey that = (ElementKey) o;

            return bucketName.equals(that.bucketName) &&
                    elementId.equals(that.elementId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bucketName, elementId);
        }
    }

    static class ElementLock {
        private final InterProcessMutex mutex;
        private int lockCount = 0;

        private ElementLock(InterProcessMutex mutex) {
            this.mutex = mutex;
        }

        static ElementLock of(InterProcessMutex mutex) {
            return new ElementLock(mutex);
        }

        void incrementLockCount() {
            this.lockCount++;
        }

        void decrementLockCount() {
            this.lockCount--;
        }

        int lockCount() {
            return lockCount;
        }

        InterProcessMutex curatorLock() {
            return mutex;
        }
    }

    private final String spaceName;
    private final ZookeeperProperties properties;
    private final CuratorFramework client;
    private final ApplicationMetrics metrics;

    private final Map<ElementKey, ElementLock> locksMap = new HashMap<>();

    private ZookeeperLocker(String spaceName, ZookeeperProperties properties,
                            CuratorFramework client, ApplicationMetrics metrics) {
        this.spaceName = spaceName;
        this.client = client;
        this.metrics = metrics;
        this.properties = properties;
    }

    public static ZookeeperLocker of(String spaceName, ZookeeperProperties properties,
                                     CuratorFramework client, ApplicationMetrics metrics) {
        return new ZookeeperLocker(spaceName, properties, client, metrics);
    }

    @Override
    public void lockElement(String bucketName, String elementId) {
        lockElement(bucketName, elementId, Duration.ofMillis(properties.getLockerTimeoutMillis()));
    }

    @Override
    public void lockElement(String bucketName, String elementId, Duration timeout) {
        metrics.getElementLockingTimer(spaceName, bucketName)
                .record(() -> notMeasuredByTimeElementLocking(bucketName, elementId, timeout));
    }

    @Override
    public void unlockElement(String bucketName, String elementId) {
        metrics.getElementUnlockingTimer(spaceName, bucketName)
                .record(() -> notMeasuredByTimeElementUnlocking(bucketName, elementId));
    }

    private void notMeasuredByTimeElementLocking(String bucketName, String elementId, Duration timeout) {
        boolean acquired;
        ElementLock elementLock = locksMap.getOrDefault(
                ElementKey.of(bucketName, elementId),
                ElementLock.of(new InterProcessMutex(client, buildLockPath(bucketName, elementId))));
        try {
            acquired = elementLock.curatorLock().acquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            metrics.getLockerErrorCounter(spaceName, bucketName).increment();
            throw new ElementLockerException(e);
        }
        if (!acquired) {
            metrics.getLockerTimeoutsCounter(spaceName, bucketName).increment();
            throw new LockTimeoutException(spaceName, bucketName, elementId, timeout);
        } else {
            elementLock.incrementLockCount();
            locksMap.putIfAbsent(ElementKey.of(bucketName, elementId), elementLock);
            metrics.getLockerCounter(spaceName, bucketName).increment();
        }
    }

    private void notMeasuredByTimeElementUnlocking(String bucketName, String elementId) {
        ElementLock elementLock = locksMap.get(ElementKey.of(bucketName, elementId));
        if (elementLock == null) {
            metrics.getLockerErrorCounter(spaceName, bucketName).increment();
            throw new LockNotHoldException(spaceName, bucketName, elementId);
        }

        try {
            elementLock.curatorLock().release();
            elementLock.decrementLockCount();
            if (elementLock.lockCount() == 0) {
                locksMap.remove(ElementKey.of(bucketName, elementId));
            }
            metrics.getLockerUnlockedCounter(spaceName, bucketName).increment();
        } catch (Exception e) {
            metrics.getLockerErrorCounter(spaceName, bucketName).increment();
            throw new ElementLockerException(e);
        }
    }

    private String buildLockPath(String bucketName, String elementId) {
        return "/" + spaceName + "/" + bucketName + "/" + elementId;
    }
}
