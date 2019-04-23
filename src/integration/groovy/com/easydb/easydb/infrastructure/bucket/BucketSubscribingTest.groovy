package com.easydb.easydb.infrastructure.bucket

import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.domain.bucket.BucketEventsPublisher
import com.easydb.easydb.domain.bucket.BucketSubscriptionQuery
import com.easydb.easydb.domain.bucket.ElementEvent
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.infrastructure.bucket.graphql.QueryValidationException
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Flux

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import static com.easydb.easydb.ElementTestBuilder.builder

class BucketSubscribingTest extends IntegrationWithCleanedDatabaseSpec {

    @Autowired
    BucketEventsPublisher bucketEventsPublisher

    def danielFaderski = builder()
            .bucketName(TEST_BUCKET_NAME)
            .clearFields()
            .addField(ElementField.of("firstName", "Daniel"))
            .addField(ElementField.of("lastName", "Faderski"))
            .build()

    def janBrzechwa = builder()
            .bucketName(TEST_BUCKET_NAME)
            .clearFields()
            .addField(ElementField.of("firstName", "Jan"))
            .addField(ElementField.of("lastName", "Brzechwa"))
            .build()

    def jurekOgorek = builder()
            .bucketName(TEST_BUCKET_NAME)
            .clearFields()
            .addField(ElementField.of("firstName", "Jurek"))
            .addField(ElementField.of("lastName", "Ogórek"))
            .build()


    def setup() {
        makeBucketChanges()
        bucketService.createBucket(TEST_BUCKET_NAME)
    }

    def cleanup() {
        try {
            bucketService.removeBucket(TEST_BUCKET_NAME)
        } catch (Exception ignored) {
        }
    }

    def "should subscribe to all data changes"() {
        when:
        Flux<ElementEvent> eventFlux = bucketEventsPublisher.subscription(new BucketSubscriptionQuery(TEST_BUCKET_NAME, Optional.empty()))

        then:
        List<ElementEvent> receivedEvents = eventFlux.take(3).collectList().block(Duration.ofMillis(5000))
        receivedEvents as Set == [new ElementEvent(danielFaderski, ElementEvent.Type.CREATE), new ElementEvent(janBrzechwa, ElementEvent.Type.CREATE),
                                  new ElementEvent(jurekOgorek, ElementEvent.Type.CREATE)] as Set

    }

    def "should subscribe to changes with query using `or` operator"() {
        given:
        String query = """
            subscription {       
                elementsEvents(filter: {
                    or: [
                            {
                                fieldsFilters: [
                                    {
                                        name: "firstName"
                                        value: "Jan"
                                    }
                                ]
                            },
                            {
                                fieldsFilters: [
                                    {
                                        name: "lastName"
                                        value: "Faderski"
                                    }
                                ]
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

        makeBucketChanges()

        when:
        Flux<ElementEvent> eventFlux = bucketEventsPublisher.subscription(new BucketSubscriptionQuery(TEST_BUCKET_NAME, Optional.of(query)))

        then:
        List<ElementEvent> receivedEvents = eventFlux.take(2).collectList().block(Duration.ofMillis(5000))

        receivedEvents as Set == [new ElementEvent(danielFaderski, ElementEvent.Type.CREATE), new ElementEvent(janBrzechwa, ElementEvent.Type.CREATE)] as Set

    }

    def "should subscribe to changes with query using `and` operator"() {
        given:
        String query = """
            subscription {       
                elementsEvents(filter: {
                    and: [
                            {
                                fieldsFilters: [
                                    {
                                        name: "firstName"
                                        value: "Daniel"
                                    }
                                ]
                            },
                            {
                                fieldsFilters: [
                                    {
                                        name: "lastName"
                                        value: "Faderski"
                                    }
                                ]
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

        makeBucketChanges()

        when:
        Flux<ElementEvent> eventFlux = bucketEventsPublisher.subscription(new BucketSubscriptionQuery(TEST_BUCKET_NAME, Optional.of(query)))

        then:
        List<ElementEvent> receivedEvents = eventFlux.take(1).collectList().block(Duration.ofMillis(5000))

        receivedEvents == [new ElementEvent(danielFaderski, ElementEvent.Type.CREATE)]
    }

    def "should throw validation error in case of empty filter query"() {
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

        when:
        bucketEventsPublisher.subscription(new BucketSubscriptionQuery(TEST_BUCKET_NAME, Optional.of(query))).blockFirst()

        then:
        thrown(QueryValidationException)
    }

    def "should throw validation error in case of two operators at the same time"() {
        given:
        String query = """
            subscription {       
                elementsEvents(filter: {
                               fieldsFilters: [
                                   {
                                       name: "firstName"
                                       value: "Jan"
                                   }
                               ],
                               or: [ 
                                       {
                                           fieldsFilters: [
                                               {
                                                   name: "firstName"
                                                   value: "Jan"
                                               }
                                           ]
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

        when:
        bucketEventsPublisher.subscription(new BucketSubscriptionQuery(TEST_BUCKET_NAME, Optional.of(query))).blockFirst()

        then:
        thrown(QueryValidationException)
    }

    def "should throw validation error in case of syntax error"() {
        given:
        String query = """
            subscription {       
                elementsEvents(filter: {
                    and: [
                            {
                                fieldsFilters: [
                                    {
                                        name: "firstName"
                                        value: "Daniel"
                                    }
                                ]
                            },
                            {
                                fieldsFilters: [
                                    
                                        name: "lastName"
                                        value: "Faderski"
                                    }
                                ]
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

        when:
        bucketEventsPublisher.subscription(new BucketSubscriptionQuery(TEST_BUCKET_NAME, Optional.of(query))).blockFirst()

        then:
        thrown(QueryValidationException)
    }

    def makeBucketChanges() {
        Executors.newSingleThreadScheduledExecutor().schedule({
            bucketService.addElement(danielFaderski)
            bucketService.addElement(janBrzechwa)
            bucketService.addElement(jurekOgorek)
        }, 100, TimeUnit.MILLISECONDS)
    }
}
