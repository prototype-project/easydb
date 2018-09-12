package com.easydb.easydb.infrastructure.locker;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.domain.locker.ElementsLocker;
import com.easydb.easydb.domain.locker.LockNotHoldException;
import com.easydb.easydb.domain.locker.LockTimeoutException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

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

    private final String spaceName;
    private final CuratorFramework client;
    private final ApplicationMetrics metrics;

    // TODO think about clearing this map in case of errors during transaction, is it needed to be thread local ?
    private final ThreadLocal<ConcurrentHashMap<ElementKey, InterProcessSemaphoreMutex>> locksMap =
            new ThreadLocal<>();

    private ZookeeperLocker(String spaceName, CuratorFramework client, ApplicationMetrics metrics) {
        this.spaceName = spaceName;
        this.client = client;
        this.metrics = metrics;
        locksMap.set(new ConcurrentHashMap<>());
    }

    public static ZookeeperLocker of(String spaceName, CuratorFramework client, ApplicationMetrics metrics) {
        return new ZookeeperLocker(spaceName, client, metrics);
    }

    @Override
    public void lockElement(String bucketName, String elementId) {
        // TODO reentrant lock needed
        lockElement(bucketName, elementId, Duration.ofMillis(0));
    }

    @Override
    public void lockElement(String bucketName, String elementId, Duration timeout) {
        boolean acquired;
        InterProcessSemaphoreMutex curatorLock = new InterProcessSemaphoreMutex(
                client, buildLockPath(bucketName, elementId));
        try {
            acquired = curatorLock.acquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            metrics.getLockerErrorCounter(spaceName, bucketName).increment();
            throw new ElementLockerException(e);
        }
        if (!acquired) {
            metrics.getLockerTimeoutsCounter(spaceName, bucketName).increment();
            throw new LockTimeoutException(spaceName, bucketName, elementId, timeout);
        } else {
            locksMap.get().put(ElementKey.of(bucketName, elementId), curatorLock);
        }
    }

    @Override
    public void unlockElement(String bucketName, String elementId) {
        InterProcessSemaphoreMutex curatorLock = locksMap.get().remove(ElementKey.of(bucketName, elementId));
        if (curatorLock == null) {
            metrics.getLockerErrorCounter(spaceName, bucketName);
            throw new LockNotHoldException(spaceName, bucketName, elementId);
        }

        try {
            curatorLock.release();
        } catch (Exception e) {
            metrics.getLockerErrorCounter(spaceName, bucketName);
            throw new ElementLockerException(e);
        }
    }

    private String buildLockPath(String bucketName, String elementId) {
        return "/" + spaceName + "/" + bucketName + "/" + elementId;
    }
}
