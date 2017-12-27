package com.easydb.easydb

import com.easydb.easydb.config.SpaceConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@SpringBootTest(
        classes = [EasydbApplication],
        properties = "application.environment=integration",
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = [SpaceConfig])
abstract class BaseSpec extends Specification {

    RestTemplate restTemplate = new RestTemplate()

    @Value('${local.server.port}')
    int port

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
}
