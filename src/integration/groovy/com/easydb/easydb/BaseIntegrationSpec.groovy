package com.easydb.easydb

import org.apache.curator.test.TestingServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@SpringBootTest(
        classes = [EasydbApplication],
        properties = ["application.environment=integration", "spring.main.allow-bean-definition-overriding=true"],
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@DirtiesContext
abstract class BaseIntegrationSpec extends Specification {

    static ZOOKEEPER_PORT = 2182

    RestTemplate restTemplate = new RestTemplate()

    TestingServer zookeeperServer

    @Value('${local.server.port}')
    int port

    void setupSpec() {
        startZookeeperServer()
    }

    void cleanupSpec() {
        stopZookeeperServer()
    }

    String localUrl(String endpoint) {
        return "http://localhost:$port$endpoint"
    }

    HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8)
        headers
    }

    HttpEntity httpJsonEntity(String jsonBody) {
        new HttpEntity<String>(jsonBody, headers())
    }

    void startZookeeperServer() {
        if (zookeeperServer == null) {
            zookeeperServer = new TestingServer(ZOOKEEPER_PORT, true)
        }
        else {
            zookeeperServer.start()
        }
    }

    void stopZookeeperServer() {
        if (zookeeperServer != null) {
            zookeeperServer.stop()
        }
    }
}
