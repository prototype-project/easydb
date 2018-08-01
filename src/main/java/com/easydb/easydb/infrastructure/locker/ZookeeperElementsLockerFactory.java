package com.easydb.easydb.infrastructure.locker;

import com.easydb.easydb.domain.locker.ElementsLocker;
import com.easydb.easydb.domain.locker.ElementsLockerFactory;
import org.apache.curator.framework.CuratorFramework;

public class ZookeeperElementsLockerFactory implements ElementsLockerFactory {

    private final CuratorFramework client;

    public ZookeeperElementsLockerFactory(CuratorFramework client) {
        this.client = client;
    }

    @Override
    public ElementsLocker build(String spaceName) {
        return ZookeeperLocker.of(spaceName, client);
    }
}
