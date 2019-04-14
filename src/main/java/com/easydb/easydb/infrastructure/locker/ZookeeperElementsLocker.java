package com.easydb.easydb.infrastructure.locker;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.config.ZookeeperProperties;
import com.easydb.easydb.domain.BucketName;
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

public class ZookeeperElementsLocker implements ElementsLocker {

    static class ElementKey {
        private final BucketName bucketName;
        private final String elementId;

        private ElementKey(BucketName bucketName, String elementId) {
            this.bucketName = bucketName;
            this.elementId = elementId;
        }

        public static ElementKey of(BucketName bucketName, String elementId) {
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

    private final ZookeeperProperties properties;
    private final CuratorFramework client;
    private final ApplicationMetrics metrics;

    private final Map<ElementKey, ElementLock> locksMap = new HashMap<>();

    public ZookeeperElementsLocker(ZookeeperProperties properties,
                                    CuratorFramework client, ApplicationMetrics metrics) {
        this.client = client;
        this.metrics = metrics;
        this.properties = properties;
    }

    @Override
    public void lockElement(BucketName bucketName, String elementId) {
        lockElement(bucketName, elementId, Duration.ofMillis(properties.getLockerTimeoutMillis()));
    }

    @Override
    public void lockElement(BucketName bucketName, String elementId, Duration timeout) {
        metrics.elementLockingTimer(bucketName.getSpaceName(), bucketName.getName()).record(
                () -> lockWithoutTimer(bucketName, elementId, timeout));
    }

    @Override
    public void unlockElement(BucketName bucketName, String elementId) {
        metrics.elementUnlockingTimer(bucketName.getSpaceName(), bucketName.getName()).record(
                () -> unlockWithoutTimer(bucketName, elementId));
    }

    private void lockWithoutTimer(BucketName bucketName, String elementId, Duration timeout) {
        boolean acquired;
        ElementLock elementLock = locksMap.getOrDefault(
                ElementKey.of(bucketName, elementId),
                ElementLock.of(new InterProcessMutex(client, buildLockPath(bucketName, elementId))));
        try {
            acquired = elementLock.curatorLock().acquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            metrics.elementLockerErrorCounter(bucketName.getSpaceName(), bucketName.getName()).increment();
            throw new UnexpectedLockerException(e);
        }
        if (!acquired) {
            metrics.elementLockerTimeoutsCounter(bucketName.getSpaceName(), bucketName.getName()).increment();
            throw new LockTimeoutException(bucketName, elementId, timeout);
        } else {
            elementLock.incrementLockCount();
            locksMap.putIfAbsent(ElementKey.of(bucketName, elementId), elementLock);
        }
        metrics.elementsLockerCounter(bucketName.getSpaceName(), bucketName.getName()).increment();
    }

    private void unlockWithoutTimer(BucketName bucketName, String elementId) {
        ElementLock elementLock = locksMap.get(ElementKey.of(bucketName, elementId));
        if (elementLock == null) {
            metrics.elementLockerErrorCounter(bucketName.getSpaceName(), bucketName.getName()).increment();
            throw new LockNotHoldException(buildLockPath(bucketName, elementId));
        }

        try {
            elementLock.curatorLock().release();
            elementLock.decrementLockCount();
            if (elementLock.lockCount() == 0) {
                locksMap.remove(ElementKey.of(bucketName, elementId));
            }
            metrics.elementsLockerUnlockedCounter(bucketName.getSpaceName(), bucketName.getName()).increment();
        } catch (Exception e) {
            metrics.elementLockerErrorCounter(bucketName.getSpaceName(), bucketName.getName()).increment();
            throw new UnexpectedLockerException(e);
        }
    }

    private String buildLockPath(BucketName bucketName, String elementId) {
        return "/" + bucketName.getSpaceName() + "/" + bucketName.getName() + "/" + elementId;
    }
}
