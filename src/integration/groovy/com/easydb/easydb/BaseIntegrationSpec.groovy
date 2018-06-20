package com.easydb.easydb

import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.api.SpaceDefinitionApiDto
import org.apache.curator.test.TestingServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@SpringBootTest(
        classes = [EasydbApplication],
        properties = "application.environment=integration",
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ContextConfiguration(classes = SpaceTestConfig)
@ActiveProfiles("integration")
abstract class BaseIntegrationSpec extends Specification {

    static ZOOKEEPER_PORT = 2181

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

    ResponseEntity<ElementQueryApiDto> addSampleElement(String spaceName, String bucketName, String body) {
        return restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/'+ bucketName),
                HttpMethod.POST,
                httpJsonEntity(body),
                ElementQueryApiDto.class)
    }

    ResponseEntity<SpaceDefinitionApiDto> addSampleSpace() {
        return restTemplate.postForEntity(
                localUrl("/api/v1/spaces/"),
                Void,
                SpaceDefinitionApiDto.class)
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
