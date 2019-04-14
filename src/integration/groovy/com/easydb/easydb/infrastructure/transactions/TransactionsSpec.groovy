package com.easydb.easydb.infrastructure.transactions

import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.ElementUtils
import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.OperationTestBuilder
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.SpaceRemovalService
import com.easydb.easydb.domain.transactions.Operation
import com.easydb.easydb.domain.transactions.OperationResult
import com.easydb.easydb.domain.transactions.TransactionAbortedException
import com.easydb.easydb.domain.transactions.PersistentTransactionManager
import com.easydb.easydb.domain.transactions.TransactionDoesNotExistException
import com.easydb.easydb.domain.transactions.TransactionRepository
import org.springframework.beans.factory.annotation.Autowired


class TransactionsSpec extends IntegrationWithCleanedDatabaseSpec implements ElementUtils {

    @Autowired
    PersistentTransactionManager transactionManager

    @Autowired
    TransactionRepository transactionRepository

    @Autowired
    SpaceRemovalService spaceRemovalService

    @Autowired
    SpaceRepository spaceRepository

    @Autowired
    BucketService bucketService

    def setup() {
        bucketService.createBucket(TEST_BUCKET_NAME)
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
        String transactionId = transactionManager.beginTransaction(TEST_BUCKET_NAME.spaceName)
        Operation modifyOperation = OperationTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME.name)
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
        String transactionId = transactionManager.beginTransaction(TEST_BUCKET_NAME.spaceName)
        Operation readOperation = OperationTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME.name)
                .type(Operation.OperationType.READ)
                .elementId(element.id)
                .build()
        OperationResult readResult = transactionManager.addOperation(transactionId, readOperation)

        then:
        readResult.element.isPresent()
        readResult.element.get().fields[0].value == '0'

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

    def "should prevent dirty writes with concurrent removal"() {
        given: "saved element"
        Element element = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .fields([ElementField.of("counter", "0")])
                .build()
        bucketService.addElement(element)

        when: "one transaction wants to update element"
        String transactionId = transactionManager.beginTransaction(TEST_BUCKET_NAME.spaceName)
        Operation updateOperation = OperationTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME.name)
                .type(Operation.OperationType.UPDATE)
                .elementId(element.id)
                .build()
        OperationResult updateResult = transactionManager.addOperation(transactionId, updateOperation)

        then:
        !updateResult.element.isPresent()

        and: "meantime element is deleted by another transaction"
        bucketService.removeElement(TEST_BUCKET_NAME, element.id)

        when: "when first transaction commits it should prevent dirty write and abort"
        transactionManager.commitTransaction(transactionId)

        then:
        thrown(TransactionAbortedException)
    }

    def "should prevent dirty writes with concurrent update"() {
        given: "saved element"
        Element element = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .fields([ElementField.of("counter", "0")])
                .build()
        bucketService.addElement(element)

        when: "one transaction read element"
        String transactionId = transactionManager.beginTransaction(TEST_BUCKET_NAME.spaceName)
        Operation readOperation = OperationTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME.name)
                .type(Operation.OperationType.READ)
                .elementId(element.id)
                .build()
        OperationResult readResult = transactionManager.addOperation(transactionId, readOperation)

        then:
        readResult.element.isPresent()

        and: "meantime element is updated by another transaction"
        bucketService.updateElement(ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .id(element.id)
                .fields([ElementField.of("counter", "1")])
                .build())

        and: "the first transaction wants to update element based on previously retrieved value"
        Operation updateOperation = OperationTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME.name)
                .type(Operation.OperationType.UPDATE)
                .elementId(element.id)
                .build()
        OperationResult updateResult = transactionManager.addOperation(transactionId, updateOperation)

        then:
        !updateResult.element.isPresent()

        when: "when first transaction commits it should prevent dirty write and abort"
        transactionManager.commitTransaction(transactionId)

        then:
        thrown(TransactionAbortedException)
    }

    def "should remove transaction after commit"() {
        given:
        def transactionId = transactionManager.beginTransaction(TEST_BUCKET_NAME.spaceName)
        transactionManager.commitTransaction(transactionId)

        when:
        transactionRepository.get(transactionId)

        then:
        thrown(TransactionDoesNotExistException)
    }
}
