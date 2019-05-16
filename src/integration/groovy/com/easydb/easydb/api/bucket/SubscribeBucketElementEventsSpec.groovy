package com.easydb.easydb.api.bucket

import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.IntegrationDatabaseSpec
import com.easydb.easydb.TestHttpOperations
import com.easydb.easydb.domain.bucket.ElementEvent
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.FluxExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SubscribeBucketElementEventsSpec extends IntegrationDatabaseSpec implements TestHttpOperations {

    static TIMEOUT = Duration.ofSeconds(5)

    WebTestClient testClient
    private String spaceName

    def setup() {
        spaceName = addSampleSpace().body.spaceName
        createTestBucket(spaceName)

        testClient = WebTestClient
                .bindToServer()
                .baseUrl(localUrl("/api/v1/spaces/$spaceName/buckets/$TEST_BUCKET_NAME/element-events"))
                .build()

    }

    def "should subscribe to queried events"() {
        given:
        ElementQueryDto element1
        ElementQueryDto element2

        Executors.newSingleThreadScheduledExecutor().schedule({
            element1 = addElementToTestBucket(spaceName, buildElementBody(ElementTestBuilder.builder().build())).getBody()
            element2 = addElementToTestBucket(spaceName, buildElementBody(ElementTestBuilder.builder().build())).getBody()
        }, 500, TimeUnit.MILLISECONDS)

        when:
        FluxExchangeResult<ElementEventDto> result = testClient.get().exchange()
                .expectStatus().isOk()
                .expectHeader().valueMatches("Content-Type", ".*${MediaType.TEXT_EVENT_STREAM_VALUE}.*")
                .returnResult(ElementEventDto)

        List<ElementEventDto> events = result.getResponseBody().take(2).collectList().block(TIMEOUT)

        then:
        events as Set == [new ElementEventDto(element1, ElementEvent.Type.CREATE),
                          new ElementEventDto(element2, ElementEvent.Type.CREATE)] as Set
    }
}
