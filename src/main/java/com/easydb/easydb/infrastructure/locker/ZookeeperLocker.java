package com.easydb.easydb.infrastructure.locker;

import com.easydb.easydb.domain.locker.LockNotHoldException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

class ZookeeperLocker {
    private final CuratorFramework client;

    private final ThreadLocal<Map<String, InterProcessSemaphoreMutex>> locksMap =
            ThreadLocal.withInitial(HashMap::new);

    ZookeeperLocker(CuratorFramework client) {
        this.client = client;
    }

    // TODO metrics on latency
    boolean lockOnPath(String path, Duration timeout) throws Exception {
        InterProcessSemaphoreMutex curatorLock = new InterProcessSemaphoreMutex(client, path);
        if (curatorLock.acquire(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
            locksMap.get().put(path, curatorLock);
            return true;
        }
        return false;
    }

    void unlockOnPath(String path) throws Exception {
        InterProcessSemaphoreMutex curatorLock = locksMap.get().remove(path);
        if (curatorLock == null) {
            throw new LockNotHoldException(path);
        }

        curatorLock.release();
    }
}
