package com.easydb.easydb.api.transactions

import com.easydb.easydb.ApiIntegrationWithAutoCreatedSpace
import com.easydb.easydb.ElementCrudDtoTestBuilder
import com.easydb.easydb.OperationDtoTestBuilder
import com.easydb.easydb.api.bucket.ElementFieldDto
import com.easydb.easydb.api.transaction.OperationResultDto
import com.easydb.easydb.api.transaction.TransactionDto
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.transactions.Operation
import com.easydb.easydb.domain.transactions.TransactionKey
import com.easydb.easydb.domain.transactions.TransactionRepository
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

import java.util.stream.Collectors


class TransactionControllerSpec extends ApiIntegrationWithAutoCreatedSpace {

    @Autowired
    BucketService bucketService

    @Autowired
    TransactionRepository repository

    def setup() {
        createTestBucket(spaceName)
    }

    def "should create transaction with ACTIVE status"() {
        when:
        ResponseEntity<TransactionDto> response = beginTransaction(spaceName)

        then:
        response.statusCodeValue == 201
        repository.get(TransactionKey.of(spaceName, response.body.transactionId)) != null
    }

    def "should add operation to transaction"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationDtoTestBuilder
                .builder()
                .type(Operation.OperationType.CREATE)
                .fields([new ElementFieldDto("name", "Daniel")])
                .elementId(null)
                .bucketName(TEST_BUCKET_NAME)
                .build()

        when:
        ResponseEntity<OperationResultDto> response = addOperation(spaceName, transactionId, operation)

        then:
        response.statusCode == HttpStatus.CREATED
        def operations = repository.get(TransactionKey.of(spaceName, transactionId)).operations
        operations.size() == 1
        operations[0].type == operation.type
        operations[0].bucketName == operation.bucketName
        operations[0].fields == operation.fields.stream().map({ it.toDomain() }).collect(Collectors.toList())
    }

    def "should throw 400 when adding create operation with given element id"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationDtoTestBuilder
                .builder()
                .type(Operation.OperationType.CREATE)
                .build()

        when:
        addOperation(spaceName, transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw 400 when adding operation other than create without element id"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationDtoTestBuilder.builder()
                .type(Operation.OperationType.UPDATE)
                .elementId(null)
                .build()

        when:
        addOperation(spaceName, transactionId, operation)

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
                localUrl("/api/v1/spaces/${spaceName}/transactions/${transactionId}/add-operation"),
                HttpMethod.POST,
                httpJsonEntity(body),
                Void.class)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw 400 when adding update operation without fields given"() {
        given:
        def element = ElementCrudDtoTestBuilder.builder().build()

        def elementId = addElement(spaceName, TEST_BUCKET_NAME, element).body.id

        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationDtoTestBuilder.builder()
                .type(Operation.OperationType.UPDATE)
                .bucketName(TEST_BUCKET_NAME)
                .elementId(elementId)
                .fields([])
                .build()

        when:
        addOperation(spaceName, transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw 400 when adding create operation without fields given"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationDtoTestBuilder.builder()
                .type(Operation.OperationType.CREATE)
                .bucketName(TEST_BUCKET_NAME)
                .elementId(null)
                .fields([])
                .build()

        when:
        addOperation(spaceName, transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw 400 when adding operation with invalid fields"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operationWithoutFieldName = OperationDtoTestBuilder.builder()
                .type(Operation.OperationType.CREATE)
                .bucketName(TEST_BUCKET_NAME)
                .elementId(null)
                .fields([new ElementFieldDto("", "test")])
                .build()

        def operationWithoutFieldValue = OperationDtoTestBuilder.builder()
                .type(Operation.OperationType.CREATE)
                .bucketName(TEST_BUCKET_NAME)
                .elementId(null)
                .fields([new ElementFieldDto("test", "")])
                .build()

        when:
        addOperation(spaceName, transactionId, operationWithoutFieldName)

        then:
        def responseOperationWithoutFieldName = thrown(HttpClientErrorException)
        responseOperationWithoutFieldName.statusCode == HttpStatus.BAD_REQUEST

        when:
        addOperation(spaceName, transactionId, operationWithoutFieldValue)

        then:
        def responseOperationWithoutFieldValue = thrown(HttpClientErrorException)
        responseOperationWithoutFieldValue.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should commit transaction"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId
        def element1ToCreate = ElementCrudDtoTestBuilder.builder()
                .fields([new ElementFieldDto("name", "Antek")])
                .build()
        def element2ToCreate = ElementCrudDtoTestBuilder.builder()
                .fields([new ElementFieldDto("name", "Grażynka")])
                .build()

        def element1Id = addElement(spaceName, TEST_BUCKET_NAME, element1ToCreate).body.id
        def element2Id = addElement(spaceName, TEST_BUCKET_NAME, element2ToCreate).body.id

        assert getElementsFromTestBucket(spaceName).results.size() == 2

        def operationUpdateElement1 = OperationDtoTestBuilder.builder()
                .type(Operation.OperationType.UPDATE)
                .elementId(element1Id)
                .bucketName(TEST_BUCKET_NAME)
                .fields([new ElementFieldDto("name", "Tadzik"), new ElementFieldDto("age", "30")])
                .build()

        def operationDeleteElement2 = OperationDtoTestBuilder.builder()
                .type(Operation.OperationType.DELETE)
                .bucketName(TEST_BUCKET_NAME)
                .elementId(element2Id)
                .build()

        addOperation(spaceName, transactionId, operationUpdateElement1)
        addOperation(spaceName, transactionId, operationDeleteElement2)

        when:
        commitTransaction(spaceName, transactionId)

        then:
        getElementsFromTestBucket(spaceName).results.size() == 1
        getElement(spaceName, TEST_BUCKET_NAME, element1Id).body.fields.toSet() ==
                [new ElementFieldDto("name", "Tadzik"), new ElementFieldDto("age", "30")].toSet()
    }

    def "should return operation read result"() {
        given:
        def element = ElementCrudDtoTestBuilder.builder().build()

        def elementDto = addElement(spaceName, TEST_BUCKET_NAME, element).body

        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationDtoTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .type(Operation.OperationType.READ)
                .elementId(elementDto.id)
                .build()
        when:
        OperationResultDto result = addOperation(spaceName, transactionId, operation).body

        then:
        result.element.isPresent()
        result.element.get().fields == elementDto.fields
    }

    def "should throw 404 when adding operation to not existing space"() {
        def operation = OperationDtoTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .build()

        when:
        restTemplate.exchange(
                localUrl("/api/v1/spaces/notExisting/transactions/whatever/add-operation"),
                HttpMethod.POST,
                httpJsonEntity(buildOperationBody(operation)),
                Void.class)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
        response.responseBodyAsString =~ "SPACE_DOES_NOT_EXIST"
    }

    def "should throw 404 when adding operation for not existing bucket"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationDtoTestBuilder.builder()
                .type(Operation.OperationType.DELETE)
                .bucketName("notExistingBucket")
                .build()

        when:
        addOperation(spaceName, transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
        response.responseBodyAsString =~ "BUCKET_DOES_NOT_EXIST"
    }

    def "should throw 404 when adding operation for not existing element"() {
        given:
        def transactionId = beginTransaction(spaceName).body.transactionId

        def operation = OperationDtoTestBuilder.builder()
                .type(Operation.OperationType.UPDATE)
                .bucketName(TEST_BUCKET_NAME)
                .build()

        when:
        addOperation(spaceName, transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
        response.responseBodyAsString =~ "ELEMENT_DOES_NOT_EXIST"
    }

    def "should throw 404 when creating transaction for not existing space"() {
        when:
        beginTransaction("notExistingSpace")

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
        response.responseBodyAsString =~ "SPACE_DOES_NOT_EXIST"
    }

    def "should throw 404 when adding operation to not existing transaction"() {
        given:
        def element = ElementCrudDtoTestBuilder.builder().build()

        String elementId = addElement(spaceName, TEST_BUCKET_NAME, element).body.id
        def operation = OperationDtoTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .elementId(elementId)
                .build()

        when:
        restTemplate.exchange(
                localUrl("/api/v1/spaces/${spaceName}/transactions/notExistingTransaction/add-operation"),
                HttpMethod.POST,
                httpJsonEntity(buildOperationBody(operation)),
                Void.class)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
        response.responseBodyAsString =~ "TRANSACTION_DOES_NOT_EXIST"
    }

    def "should throw 404 when committing transaction in not existing space"() {
        when:
        commitTransaction("not existing space", "whatever")

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
        response.responseBodyAsString =~ "SPACE_DOES_NOT_EXIST"
    }

    def "should throw 404 when committing not existing transaction"() {
        when:
        commitTransaction(spaceName, "notExistingTransactionId")

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
        response.responseBodyAsString =~ "TRANSACTION_DOES_NOT_EXIST"
    }
}
