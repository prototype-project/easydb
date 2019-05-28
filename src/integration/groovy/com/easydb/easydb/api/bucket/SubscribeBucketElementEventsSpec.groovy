package com.easydb.easydb.api.bucket

import com.easydb.easydb.ApiIntegrationWithAutoCreatedSpace
import com.easydb.easydb.ElementCrudDtoTestBuilder
import com.easydb.easydb.domain.bucket.ElementEvent
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.FluxExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.yaml.snakeyaml.util.UriEncoder

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SubscribeBucketElementEventsSpec extends ApiIntegrationWithAutoCreatedSpace {

    static TIMEOUT = Duration.ofSeconds(5)

    def setup() {
        createTestBucket(spaceName)
    }

    def "should subscribe to all events"() {
        given:
        ElementQueryDto element1
        ElementQueryDto element2

        Executors.newSingleThreadScheduledExecutor().schedule({
            element1 = addElementToTestBucket(spaceName, buildElementBody(ElementCrudDtoTestBuilder.builder().build())).getBody()
            element2 = addElementToTestBucket(spaceName, buildElementBody(ElementCrudDtoTestBuilder.builder().build())).getBody()
        }, 500, TimeUnit.MILLISECONDS)

        when:
        FluxExchangeResult<ElementEventDto> result = createWebClient().get().exchange()
                .expectStatus().isOk()
                .expectHeader().valueMatches("Content-Type", ".*${MediaType.TEXT_EVENT_STREAM_VALUE}.*")
                .returnResult(ElementEventDto)

        List<ElementEventDto> events = result.getResponseBody().take(2).collectList().block(TIMEOUT)

        then:
        events as Set == [new ElementEventDto(element1, ElementEvent.Type.CREATE),
                          new ElementEventDto(element2, ElementEvent.Type.CREATE)] as Set
    }

    def "should subscribe to changes with query"() {
        given:
        String query = """
            subscription {       
                elementsEvents(filter: {
                    fieldsFilters: [
                        {
                            name: "foo"
                            value: "bar"
                        }
                    ]
            }) { 
                    type         
                    element {    
                        id       
                        fields { 
                            name 
                            value
                        }        
                    }            
                }                
            }               
        """

        ElementQueryDto element1
        ElementQueryDto element2

        Executors.newSingleThreadScheduledExecutor().schedule({
            element1 = addElementToTestBucket(spaceName, buildElementBody(
                    ElementCrudDtoTestBuilder.builder().build())
            ).getBody()

            element2 = addElementToTestBucket(spaceName, buildElementBody(
                    ElementCrudDtoTestBuilder.builder().clearFields()
                            .addField(new ElementFieldDto("foo", "bar"))
                            .build())).getBody()
        }, 500, TimeUnit.MILLISECONDS)

        when:
        FluxExchangeResult<ElementEventDto> result = createWebClient(query).get().exchange()
                .expectStatus().isOk()
                .expectHeader().valueMatches("Content-Type", ".*${MediaType.TEXT_EVENT_STREAM_VALUE}.*")
                .returnResult(ElementEventDto)

        List<ElementEventDto> events = result.getResponseBody().take(1).collectList().block(TIMEOUT)

        then:
        events as Set == [new ElementEventDto(element2, ElementEvent.Type.CREATE)] as Set
    }

    def "should throw 400 when subscribing with invalid query"() {
        given:
        String query = """
            subscription {       
                elementsEvents(unknown: {
            }) { 
                    type         
                    element {    
                        id       
                        fields { 
                            name 
                            value
                        }        
                    }            
                }                
            }               
        """

        expect:
        createWebClient(query).get().exchange().expectStatus().isBadRequest()
    }

    def createWebClient(String query = "") {
        String localUrl = !query.isEmpty() ? localUrl("/api/v1/spaces/$spaceName/buckets/$TEST_BUCKET_NAME/element-events?query=${UriEncoder.encode(query)}") :
                localUrl("/api/v1/spaces/$spaceName/buckets/$TEST_BUCKET_NAME/element-events")

        return WebTestClient
                .bindToServer()
                .baseUrl(localUrl)
                .build()
    }
}
