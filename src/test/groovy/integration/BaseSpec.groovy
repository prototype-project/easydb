package integration

import com.easydb.easydb.EasydbApplication
import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.api.SpaceDefinitionApiDto
import integration.space.SpaceTestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@SpringBootTest(
        classes = [EasydbApplication],
        properties = "application.environment=integration",
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = SpaceTestConfig)
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

    protected ResponseEntity<ElementQueryApiDto> addSampleElement(String spaceName, String bucketName, String body) {
        return restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/'+ bucketName),
                HttpMethod.POST,
                httpJsonEntity(body),
                ElementQueryApiDto.class)
    }

    protected ResponseEntity<SpaceDefinitionApiDto> addSampleSpace() {
        return restTemplate.postForEntity(
                localUrl("/api/v1/spaces/"),
                Void,
                SpaceDefinitionApiDto.class)
    }
}
