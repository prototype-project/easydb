package com.easydb.easydb.infrastructure.transactions

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.BucketServiceFactory
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceService
import com.easydb.easydb.domain.transactions.Operation
import com.easydb.easydb.domain.transactions.TransactionManager
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TransactionsSpec extends BaseIntegrationSpec {

    static TEST_SPACE = "testSpaceTransactions"
    static TEST_BUCKET = "testBucketTransactions"
    static THREADS = 10

    @Shared
    ExecutorService executor = Executors.newFixedThreadPool(THREADS)

    @Autowired
    TransactionManager transactionManager

    @Autowired
    SpaceService spaceService

    @Autowired
    BucketServiceFactory bucketServiceFactory

    BucketService bucketService

    def setup() {
        spaceService.save(Space.of(TEST_SPACE))
        bucketService = bucketServiceFactory.buildBucketService(TEST_SPACE)
    }

    def "should perform serializable transaction"() {
        given: "saved counter element with value 0"
        Element element = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET)
                .fields([ElementField.of("counter", "0")])
                .build()
        bucketService.addElement(element)

        when: "multiple transactions increment the same counter element concurrently"
        executor.submit({
            1000.times {
                String transactionId = transactionManager.beginTransaction(TEST_SPACE)
                def updatedCounter = Integer.parseInt(bucketService.getElement(TEST_BUCKET, element.id).getFieldValue("counter"))++
                Element updated = ElementTestBuilder.builder()
                    .bucketName(TEST_BUCKET)
                    .fields([ElementField.of("counter", updatedCounter.toString())])
                    .build()

                transactionManager.addOperation(transactionId, Operation.of(Operation.OperationType.UPDATE, updated))

                transactionManager.commitTransaction(transactionId)
            }
        })

        then:
        assert executor.awaitTermination(10, TimeUnit.SECONDS)
        Integer.parseInt(bucketService.getElement(TEST_BUCKET, element.id).getFieldValue("counter")) == 1000 * THREADS
    }
}
