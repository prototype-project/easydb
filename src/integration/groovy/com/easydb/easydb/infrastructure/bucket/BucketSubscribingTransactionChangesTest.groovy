package com.easydb.easydb.infrastructure.bucket

import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.domain.bucket.BucketEventsPublisher
import com.easydb.easydb.domain.bucket.BucketSubscriptionQuery
import com.easydb.easydb.domain.bucket.ElementEvent
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.space.UUIDProvider
import com.easydb.easydb.domain.transactions.Operation
import com.easydb.easydb.domain.transactions.PersistentTransactionManager
import com.easydb.easydb.domain.transactions.TransactionKey
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Flux

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import static com.easydb.easydb.ElementTestBuilder.builder

class BucketSubscribingTransactionChangesTest extends IntegrationWithCleanedDatabaseSpec {

    @Autowired
    BucketEventsPublisher bucketEventsPublisher

    @Autowired
    PersistentTransactionManager transactionManager

    UUIDProvider uuidProvider = new UUIDProvider()

    def danielFaderski = builder()
            .bucketName(TEST_BUCKET_NAME)
            .clearFields()
            .addField(ElementField.of("firstName", "Daniel"))
            .addField(ElementField.of("lastName", "Faderski"))
            .id(uuidProvider.generateUUID())
            .build()

    def janBrzechwa = builder()
            .bucketName(TEST_BUCKET_NAME)
            .clearFields()
            .addField(ElementField.of("firstName", "Jan"))
            .addField(ElementField.of("lastName", "Brzechwa"))
            .id(uuidProvider.generateUUID())
            .build()

    def jurekOgorek = builder()
            .bucketName(TEST_BUCKET_NAME)
            .clearFields()
            .addField(ElementField.of("firstName", "Jurek"))
            .addField(ElementField.of("lastName", "Ogórek"))
            .id(uuidProvider.generateUUID())
            .build()

    def setup() {
        bucketService.createBucket(TEST_BUCKET_NAME)
        makeBucketChangesUnderTransaction()
    }

    def cleanup() {
        try {
            bucketService.removeBucket(TEST_BUCKET_NAME)
        } catch (Exception ignored) {
        }
    }

    def "should subscribe to data changes under transaction and with specific query"() {
        given:
        String query = """
            subscription {       
                elementsEvents(filter: {
                    and: [
                        {
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
                        }
                        {
                            or: [
                                    {
                                        fieldsFilters: [
                                            {
                                                name: "firstName"
                                                value: "Jurek"
                                            }
                                        ]
                                    },
                                    {
                                        fieldsFilters: [
                                            {
                                                name: "lastName"
                                                value: "Brzechwa"
                                            }
                                        ]
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
        Flux<ElementEvent> eventFlux = bucketEventsPublisher.subscription(BucketSubscriptionQuery.of(TEST_BUCKET_NAME, Optional.of(query)))

        then:
        List<ElementEvent> receivedEvents = eventFlux.take(1).collectList().block(Duration.ofSeconds(5))

        receivedEvents == [new ElementEvent(janBrzechwa, ElementEvent.Type.DELETE)]
    }

    def makeBucketChangesUnderTransaction() {
        bucketService.addElement(janBrzechwa)
        bucketService.addElement(jurekOgorek)

        String transactionId = transactionManager.beginTransaction(TEST_BUCKET_NAME.spaceName)

        transactionManager.addOperation(TransactionKey.of(TEST_BUCKET_NAME.spaceName, transactionId),
                Operation.of(Operation.OperationType.CREATE, TEST_BUCKET_NAME.name, danielFaderski.id, danielFaderski.fields))

        transactionManager.addOperation(TransactionKey.of(TEST_BUCKET_NAME.spaceName, transactionId),
                Operation.of(Operation.OperationType.DELETE, TEST_BUCKET_NAME.name, janBrzechwa.id))

        transactionManager.addOperation(TransactionKey.of(TEST_BUCKET_NAME.spaceName, transactionId),
                Operation.of(Operation.OperationType.UPDATE, TEST_BUCKET_NAME.name, jurekOgorek.id, jurekOgorek.fields))

        Executors.newSingleThreadScheduledExecutor().schedule({
            transactionManager.commitTransaction(TransactionKey.of(TEST_BUCKET_NAME.spaceName, transactionId))
        }, 300, TimeUnit.MILLISECONDS)
    }
}
