package com.easydb.easydb.infrastructure.discovery

import com.easydb.easydb.BaseIntegrationSpec
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.springframework.beans.factory.annotation.Autowired;

class DiscoveryServiceSpec extends BaseIntegrationSpec {

    @Autowired
    CuratorFramework curatorFramework

    def "should register in discovery"() {
        given:
        def serviceDiscovery = ServiceDiscoveryBuilder.builder(String.class)
                .client(curatorFramework)
                .basePath("/discovery")
                .watchInstances(false)
                .build();
        expect:
        serviceDiscovery.start()
        serviceDiscovery.queryForInstances("Easydb").size() == 1
    }
}
