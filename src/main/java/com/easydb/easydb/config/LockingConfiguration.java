package com.easydb.easydb.config;

import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import com.easydb.easydb.infrastructure.locker.factories.ZookeeperElementsLockerFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class LockingConfiguration {

    @Autowired
    ZookeeperProperties properties;

    @Bean(initMethod = "start", destroyMethod = "close")
    CuratorFramework curatorClient() {
        RetryPolicy retryPolicy = new RetryUntilElapsed(properties.getRetryTimeoutMillis(), properties.getRetrySleepMillis());

        CuratorFrameworkFactory.Builder clientBuilder = CuratorFrameworkFactory.builder();
        clientBuilder.connectionTimeoutMs(properties.getConnectionTimeoutMillis());
        clientBuilder.sessionTimeoutMs(properties.getSessionTimeoutMillis());
        clientBuilder.connectString(properties.getConnectionString());
        clientBuilder.retryPolicy(retryPolicy);
        //TODO set connection retry policy
        return clientBuilder.build();
    }

    @Bean
    ElementsLockerFactory elementsLockerFactory(CuratorFramework client) {
        return new ZookeeperElementsLockerFactory(client);
    }
}
