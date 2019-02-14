package com.easydb.easydb.config;

import com.easydb.easydb.infrastructure.discovery.ZookeeperServiceRegistrar;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DiscoveryConfig{

    @Bean
    ZookeeperServiceRegistrar zookeeperServiceRegistrar(CuratorFramework curatorFramework) {
        return new ZookeeperServiceRegistrar(curatorFramework);
    }
}