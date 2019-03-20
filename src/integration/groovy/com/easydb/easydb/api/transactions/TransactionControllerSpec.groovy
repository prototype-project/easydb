package com.easydb.easydb.api.transactions

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.OperationTestBuilder
import com.easydb.easydb.TestHttpOperations
import com.easydb.easydb.api.ElementFieldDto
import com.easydb.easydb.api.OperationResultDto
import com.easydb.easydb.api.TransactionDto
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.transactions.Operation
import com.easydb.easydb.domain.transactions.Transaction
import com.easydb.easydb.domain.transactions.TransactionRepository
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException


class TransactionControllerSpec extends BaseIntegrationSpec implements TestHttpOperations {

    @Autowired
    BucketServiceFactory bucketServiceFactory

    @Autowired
    TransactionRepository repository

    String spaceName
    BucketService bucketService

    def setup() {
        spaceName = addSampleSpace().body.spaceName
        this.bucketService = bucketServiceFactory.buildBucketService(spaceName)
        createTestBucket(spaceName)
    }

    def "should create transaction with ACTIVE status"() {
        when:
        ResponseEntity<TransactionDto> response = beginTransaction(spaceName)

        then:
        response.statusCodeValue == 201
        repository.get(response.body.transactionId) != null
    }

    def "should add operation to transaction"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationTestBuilder
                .builder()
                .type(Operation.OperationType.CREATE)
                .fields([ElementField.of("name", "Daniel")])
                .elementId(null)
                .bucketName(TEST_BUCKET_NAME)
                .build()

        when:
        ResponseEntity<OperationResultDto> response = addOperation(transactionId, operation)

        then:
        response.statusCode == HttpStatus.CREATED
        def operations = repository.get(transactionId).operations
        operations.size() == 1
        operations[0].type == operation.type
        operations[0].bucketName == operation.bucketName
        operations[0].fields == operation.fields
    }

    def "should throw 404 when adding create operation in not existing bucket"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationTestBuilder
                .builder()
                .type(Operation.OperationType.CREATE)
                .fields([ElementField.of("name", "Daniel")])
                .elementId(null)
                .bucketName("notExistingBucket")
                .build()

        when:
        addOperation(transactionId, operation)

        then:
        def ex= thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.NOT_FOUND
    }

    def "should throw 404 when creating transaction for not existing space"() {
        when:
        beginTransaction("notExistingSpace")

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should throw 404 when adding operation to not existing transaction"() {
        given:
        // create bucket and element to be sure that 404 it thrown due to missing transaction
        def element = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .build()

        String elementId = addElement(spaceName, element).body.id
        def operation = OperationTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .elementId(elementId)
                .build()

        when:
        restTemplate.exchange(
                localUrl("/api/v1/transactions/${spaceName}/add-operation/notExistingTransaction"),
                HttpMethod.POST,
                httpJsonEntity(buildOperationBody(operation)),
                Void.class)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should throw 400 when adding create operation with given element id"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationTestBuilder
                .builder()
                .type(Operation.OperationType.CREATE)
                .build()

        when:
        addOperation(transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw 400 when adding operation other than create without element id"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationTestBuilder.builder()
                .type(Operation.OperationType.UPDATE)
                .elementId(null)
                .build()

        when:
        addOperation(transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw 400 when adding operation of unknown type"() {
        def transactionId = beginTransaction(spaceName).body

        def body = JsonOutput.toJson([
                type      : "unknownType",
                fields    : [],
                bucketName: TEST_BUCKET_NAME,
                id        : "randomId"
        ])

        when:
        restTemplate.exchange(
                localUrl("/api/v1/transactions/${transactionId}/add-operation"),
                HttpMethod.POST,
                httpJsonEntity(body),
                Void.class)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw 400 when adding update operation without fields given"() {
        given:
        def element = ElementTestBuilder
                .builder()
                .bucketName(TEST_BUCKET_NAME)
                .id(null)
                .build()

        def elementId = addElement(spaceName, element).body.id

        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationTestBuilder.builder()
                .type(Operation.OperationType.UPDATE)
                .bucketName(TEST_BUCKET_NAME)
                .elementId(elementId)
                .fields([])
                .build()

        when:
        addOperation(transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw 400 when adding create operation without fields given"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationTestBuilder.builder()
                .type(Operation.OperationType.CREATE)
                .bucketName(TEST_BUCKET_NAME)
                .elementId(null)
                .fields([])
                .build()

        when:
        addOperation(transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw 400 when adding operation with invalid fields"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operationWithoutFieldName = OperationTestBuilder.builder()
                .type(Operation.OperationType.CREATE)
                .bucketName(TEST_BUCKET_NAME)
                .elementId(null)
                .fields([ElementField.of("", "test")])
                .build()

        def operationWithoutFieldValue = OperationTestBuilder.builder()
                .type(Operation.OperationType.CREATE)
                .bucketName(TEST_BUCKET_NAME)
                .elementId(null)
                .fields([ElementField.of("test", "")])
                .build()

        when:
        addOperation(transactionId, operationWithoutFieldName)

        then:
        def responseOperationWithoutFieldName = thrown(HttpClientErrorException)
        responseOperationWithoutFieldName.statusCode == HttpStatus.BAD_REQUEST

        when:
        addOperation(transactionId, operationWithoutFieldValue)

        then:
        def responseOperationWithoutFieldValue = thrown(HttpClientErrorException)
        responseOperationWithoutFieldValue.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw 404 when adding operation for not existing element"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def element = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .id(null)
                .build()

        // creates bucket implicitly
        addElement(spaceName, element)

        def operation = OperationTestBuilder.builder()
                .type(Operation.OperationType.UPDATE)
                .bucketName(TEST_BUCKET_NAME)
                .build()

        when:
        addOperation(transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should throw 404 when adding operation for not existing bucket"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationTestBuilder.builder()
                .type(Operation.OperationType.DELETE)
                .bucketName("notExistingBucket")
                .build()

        when:
        addOperation(transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should commit transaction"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId
        def element1ToCreate = ElementTestBuilder.builder()
                .fields([ElementField.of("name", "Antek")])
                .bucketName(TEST_BUCKET_NAME)
                .id(null)
                .build()
        def element2ToCreate = ElementTestBuilder.builder()
                .fields([ElementField.of("name", "Grażynka")])
                .bucketName(TEST_BUCKET_NAME)
                .id(null)
                .build()

        def element1Id = addElement(spaceName, element1ToCreate).body.id
        def element2Id = addElement(spaceName, element2ToCreate).body.id

        assert getElementsFromTestBucket(spaceName).results.size() == 2

        def operationUpdateElement1 = OperationTestBuilder.builder()
                .type(Operation.OperationType.UPDATE)
                .elementId(element1Id)
                .bucketName(TEST_BUCKET_NAME)
                .fields([ElementField.of("name", "Tadzik"), ElementField.of("age", "30")])
                .build()

        def operationDeleteElement2 = OperationTestBuilder.builder()
                .type(Operation.OperationType.DELETE)
                .bucketName(TEST_BUCKET_NAME)
                .elementId(element2Id)
                .build()

        addOperation(transactionId, operationUpdateElement1)
        addOperation(transactionId, operationDeleteElement2)

        when:
        commitTransaction(transactionId)

        then:
        getElementsFromTestBucket(spaceName).results.size() == 1
        getElement(spaceName, TEST_BUCKET_NAME, element1Id).fields.toSet() ==
                [new ElementFieldDto("name", "Tadzik"), new ElementFieldDto("age", "30")].toSet()
    }

    def "should return operation read result"() {
        given:
        def element = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .id(null)
                .build()

        def elementDto = addElement(spaceName, element).body

        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .type(Operation.OperationType.READ)
                .elementId(elementDto.id)
                .build()
        when:
        OperationResultDto result = addOperation(transactionId, operation).body

        then:
        result.element.isPresent()
        result.element.get().fields == elementDto.fields
    }
}
