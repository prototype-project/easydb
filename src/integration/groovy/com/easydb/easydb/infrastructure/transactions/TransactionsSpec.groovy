package com.easydb.easydb.infrastructure.transactions

import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.SpaceService
import com.easydb.easydb.domain.transactions.Operation
import com.easydb.easydb.domain.transactions.TransactionManager
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TransactionsSpec extends IntegrationWithCleanedDatabaseSpec {

    static THREADS = 20

    @Shared
    ExecutorService executor = Executors.newFixedThreadPool(THREADS)

    @Autowired
    TransactionManager transactionManager

    @Autowired
    SpaceService spaceService

    @Autowired
    SpaceRepository spaceRepository

    @Autowired
    BucketServiceFactory bucketServiceFactory

    BucketService bucketService


    def setup() {
        bucketService = bucketServiceFactory.buildBucketService(TEST_SPACE)
    }

    // preventing dirty read prevents one transaction to read uncommitted changes of another transaction
    def "should prevent dirty read"() {
        given: "saved counter element with value 0"
        Element element = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .fields([ElementField.of("counter", "0")])
                .build()
        bucketService.addElement(element)

        when: "one transaction lasts very long"
        executor.submit({
            String transactionId = transactionManager.beginTransaction(TEST_SPACE)
            1000000.times {
                Element updated = ElementTestBuilder.builder()
                        .id(element.id)
                        .bucketName(TEST_BUCKET_NAME)
                        .fields([ElementField.of("counter", it.toString())])
                        .build()
                transactionManager.addOperation(transactionId, Operation.of(Operation.OperationType.UPDATE, updated))
            }

            Element causingAbort = ElementTestBuilder.builder().bucketName("notExistingBucket").build()
            transactionManager.addOperation(transactionId, Operation.of(Operation.OperationType.UPDATE, causingAbort))
            transactionManager.commitTransaction(transactionId)
        })

        and: "another transaction continuously tries read modifying element"
        executor.submit({
            String transactionId = transactionManager.beginTransaction(TEST_SPACE)
            1000000.times {

            }
        })

        then:
        assert executor.awaitTermination(10, TimeUnit.SECONDS)
    }

    def "should perform read committed transaction"() {

    }

    def "should perform repeatable reads transaction"() {

    }

    def "should perform serializable transaction"() {

    }


    def "should perform atomic transaction on single element"() {
        given: "saved counter element with value 0"
        Element element = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .fields([ElementField.of("counter", "0")])
                .build()
        bucketService.addElement(element)

        when: "multiple transactions increment the same counter element concurrently"
        executor.submit({})

        then:
        assert executor.awaitTermination(1000, TimeUnit.SECONDS)
        Integer.parseInt(bucketService.getElement(TEST_BUCKET_NAME, element.id).getFieldValue("counter")) == 1000 * THREADS
    }

    def "should perform transaction on multiple elements in isolation"() {
        given: "few elements with equal values"
        Element element1 = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .fields([ElementField.of("counter", "0")])
                .build()
        Element element2 = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .fields([ElementField.of("counter", "0")])
                .build()
        Element element3 = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .fields([ElementField.of("counter", "0")])
                .build()

        bucketService.addElement(element1)
        bucketService.addElement(element2)
        bucketService.addElement(element3)

        Random random = new Random()

        when: "multiple transactions increment or decrement counter"
        executor.submit({})

        then: "all counters should be equal"
        executor.shutdown()
        assert executor.awaitTermination(2000, TimeUnit.SECONDS)
        def counter1AfterTransaction = Integer.parseInt(bucketService.getElement(TEST_BUCKET_NAME, element1.id).getFieldValue("counter"))
        def counter2AfterTransaction = Integer.parseInt(bucketService.getElement(TEST_BUCKET_NAME, element2.id).getFieldValue("counter"))
        def counter3AfterTransaction = Integer.parseInt(bucketService.getElement(TEST_BUCKET_NAME, element3.id).getFieldValue("counter"))

        counter1AfterTransaction == counter2AfterTransaction
        counter1AfterTransaction == counter3AfterTransaction
    }
}
