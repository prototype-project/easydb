package com.easydb.easydb.infrastructure.locker.factories;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.config.ZookeeperProperties;
import com.easydb.easydb.domain.locker.ElementsLocker;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import com.easydb.easydb.infrastructure.locker.ZookeeperLocker;
import org.apache.curator.framework.CuratorFramework;

public class ZookeeperElementsLockerFactory implements ElementsLockerFactory {

    private final CuratorFramework client;
    private final ApplicationMetrics metrics;
    private final ZookeeperProperties properties;

    public ZookeeperElementsLockerFactory(CuratorFramework client, ZookeeperProperties properties,
                                          ApplicationMetrics metrics) {
        this.client = client;
        this.metrics = metrics;
        this.properties = properties;
    }

    @Override
    public ElementsLocker build(String spaceName) {
        return ZookeeperLocker.of(spaceName, properties, client, metrics);
    }
}
