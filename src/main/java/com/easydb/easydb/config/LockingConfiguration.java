package com.easydb.easydb.config;

import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.LockTimeoutException;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import com.easydb.easydb.domain.transactions.Retryier;
import com.easydb.easydb.infrastructure.locker.ZookeeperBucketLocker;
import com.easydb.easydb.infrastructure.locker.ZookeeperSpaceLocker;
import com.easydb.easydb.infrastructure.locker.factories.ZookeeperElementsLockerFactory;
import java.util.Collections;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class LockingConfiguration {

    @Autowired
    ZookeeperProperties properties;

    @Bean(initMethod = "start", destroyMethod = "close")
    CuratorFramework curatorClient() {
        RetryPolicy retryPolicy = new RetryForever(properties.getRetrySleepMillis());

        CuratorFrameworkFactory.Builder clientBuilder = CuratorFrameworkFactory.builder();
        clientBuilder.connectionTimeoutMs(properties.getConnectionTimeoutMillis());
        clientBuilder.sessionTimeoutMs(properties.getSessionTimeoutMillis());
        clientBuilder.connectString(properties.getConnectionString());
        clientBuilder.retryPolicy(retryPolicy);
        return clientBuilder.build();
    }

    @Bean
    ElementsLockerFactory elementsLockerFactory(CuratorFramework client, ZookeeperProperties properties,
                                                ApplicationMetrics metrics) {
        return new ZookeeperElementsLockerFactory(client, properties, metrics);
    }

    @Bean
    BucketLocker bucketLocker(CuratorFramework client, ZookeeperProperties properties,
                                     ApplicationMetrics metrics) {
        return new ZookeeperBucketLocker(client, properties, metrics);
    }

    @Bean
    SpaceLocker spaceLocker(CuratorFramework client, ZookeeperProperties properties, ApplicationMetrics metrics) {
        return new ZookeeperSpaceLocker(client, properties, metrics);
    }

    @Bean
    Retryier lockerRetryier(LockingProperties properties) {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy boundedRetriesPolicy = new SimpleRetryPolicy(
                properties.getLockAttempts(), Collections.singletonMap(LockTimeoutException.class, true));

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(properties.getBackoffMillis());

        retryTemplate.setRetryPolicy(boundedRetriesPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return new Retryier(retryTemplate);
    }
}
