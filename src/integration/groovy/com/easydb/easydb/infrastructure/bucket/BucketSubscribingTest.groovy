package com.easydb.easydb.infrastructure.bucket

import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.domain.bucket.BucketEventsPublisher
import com.easydb.easydb.domain.bucket.BucketSubscriptionQuery
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementEvent
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Flux

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import static com.easydb.easydb.ElementTestBuilder.builder

class BucketSubscribingTest extends IntegrationWithCleanedDatabaseSpec {

    @Autowired
    BucketEventsPublisher bucketEventsPublisher

    def setup() {
        bucketService.createBucket(TEST_BUCKET_NAME)
    }

    def cleanup() {
        try {
            bucketService.removeBucket(TEST_BUCKET_NAME)
        } catch (Exception ignored) {
        }
    }

    def "should subscribe to all data changes"() {
        given:
        Element toCreate = builder().bucketName(TEST_BUCKET_NAME).build()

        Executors.newSingleThreadScheduledExecutor().schedule({
            bucketService.addElement(toCreate)
        }, 50, TimeUnit.MILLISECONDS)

        String eventsQuery = """
              subscription {
                elementsEvents {  
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
        Flux<ElementEvent> allEvents = bucketEventsPublisher.subscription(new BucketSubscriptionQuery(TEST_BUCKET_NAME, Optional.of(eventsQuery)))

        then:
        ElementEvent event = allEvents.take(1).blockFirst(Duration.ofMillis(1000))
        with(event) {
            event.type == ElementEvent.Type.CREATE
            event.element == toCreate
        }
    }
}
