package com.easydb.easydb.infrastructure.discovery;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.annotation.PostConstruct;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.springframework.beans.factory.annotation.Value;

public class ZookeeperServiceRegistrar implements ServiceRegistrar {

    private final ServiceDiscovery serviceDiscovery;
    private String host;

    @Value("${server.port}")
    private String port;


    public ZookeeperServiceRegistrar(CuratorFramework curatorFramework) {
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(String.class)
                .client(curatorFramework)
                .basePath("/discovery")
                .watchInstances(false)
                .build();
        try {
            this.host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
           throw new RuntimeException("Cannot get host address", e);
        }
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void register() {
        try {
            serviceDiscovery.start();
            serviceDiscovery.registerService(
                    ServiceInstance.builder()
                            .address(host)
                            .port(Integer.valueOf(port))
                            .serviceType(ServiceType.DYNAMIC)
                            .name("Easydb")
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Cannot register in discovery...", e);
        }
    }
}