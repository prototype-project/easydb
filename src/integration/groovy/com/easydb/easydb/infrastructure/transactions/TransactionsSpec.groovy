package com.easydb.easydb.infrastructure.transactions

import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.OperationTestBuilder
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.SpaceService
import com.easydb.easydb.domain.transactions.Operation
import com.easydb.easydb.domain.transactions.OperationResult
import com.easydb.easydb.domain.transactions.TransactionAbortedException
import com.easydb.easydb.domain.transactions.TransactionManager
import org.springframework.beans.factory.annotation.Autowired


class TransactionsSpec extends IntegrationWithCleanedDatabaseSpec {

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

        when: "one transaction modifies element"
        String transactionId = transactionManager.beginTransaction(TEST_SPACE)
        Operation modifyOperation = OperationTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .type(Operation.OperationType.UPDATE)
                .fields([ElementField.of("counter", "1")])
                .elementId(element.id)
                .build()
        transactionManager.addOperation(transactionId, modifyOperation)

        then: "element has still old value until transaction commits"
        bucketService.getElement(TEST_BUCKET_NAME, element.id).fields[0].value == '0'

        and: "transaction finally commits"
        transactionManager.commitTransaction(transactionId)

        then: "element has value changed"
        bucketService.getElement(TEST_BUCKET_NAME, element.id).fields[0].value == '1'
    }

    def "should prevent non-repeatable reads during transaction"() {
        given: "saved counter element with value 0"
        Element element = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .fields([ElementField.of("counter", "0")])
                .build()
        bucketService.addElement(element)

        when: "one transaction read element"
        String transactionId = transactionManager.beginTransaction(TEST_SPACE)
        Operation readOperation = OperationTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .type(Operation.OperationType.READ)
                .elementId(element.id)
                .build()
        OperationResult resultRead = transactionManager.addOperation(transactionId, readOperation)

        then:
        resultRead.element.isPresent()
        resultRead.element.get().fields[0].value == '0'

        and: "meantime element is updated by another transaction"
        bucketService.updateElement(ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .id(element.id)
                .fields([ElementField.of("counter", "1")])
                .build())

        when: "second read should prevent non-repeatable read and cause the transaction to abort"
        transactionManager.addOperation(transactionId, readOperation)

        then:
        thrown(TransactionAbortedException)
    }

    def "should prevent dirty writes"() {

    }
}
