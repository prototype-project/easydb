package com.easydb.easydb.api.transactions

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.OperationTestBuilder
import com.easydb.easydb.TestUtils
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.BucketServiceFactory
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


class TransactionControllerSpec extends BaseIntegrationSpec implements TestUtils {

    @Autowired
    BucketServiceFactory bucketServiceFactory

    @Autowired
    TransactionRepository repository

    String spaceName
    BucketService bucketService

    def setup() {
        spaceName = addSampleSpace().body.spaceName
        this.bucketService = bucketServiceFactory.buildBucketService(spaceName)
    }

    def "should create transaction with ACTIVE status"() {
        when:
        ResponseEntity<String> response = beginTransaction(spaceName)

        then:
        response.statusCodeValue == 201
        repository.get(response.body).getState() == Transaction.State.ACTIVE
    }

    def "should add create operation to transaction"() {
        given:
        def transactionId = beginTransaction(spaceName).body
        def element = ElementTestBuilder
                .builder()
                .clearFields()
                .addField(ElementField.of("name", "Daniel"))
                .id(null)
                .build()

        def operation = OperationTestBuilder
                .builder()
                .type(Operation.OperationType.CREATE)
                .element(element)
                .build()

        when:
        ResponseEntity<Void> response = addOperation(spaceName, transactionId, operation)

        then:
        response.statusCodeValue == 201
        def operations = repository.get(transactionId).operations
        operations.size() == 1
        operations[0].type == operation.type
        operations[0].element.bucketName == operation.element.bucketName
        operations[0].element.fields == operation.element.fields
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
        def operation = OperationTestBuilder.builder().build()

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
        def transactionId = beginTransaction(spaceName).body

        def element = ElementTestBuilder.builder().build()
        def operation = OperationTestBuilder
                .builder()
                .element(element)
                .build()

        when:
        addOperation(spaceName, transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.BAD_REQUEST

    }

    def "should throw 400 when adding operation other than create without element id"() {
        given:
        def transactionId = beginTransaction(spaceName).body
        def element = ElementTestBuilder.builder()
                .id(null)
                .build()

        def operation = OperationTestBuilder.builder()
                .type(Operation.OperationType.UPDATE)
                .element(element)
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
                type: "unknownType",
                element: [
                        fields: [],
                        bucketName: TEST_BUCKET_NAME,
                        id: "randomId"
                ]
        ])

        when:
        restTemplate.exchange(
                localUrl("/api/v1/transactions/${spaceName}/add-operation/${transactionId}"),
                HttpMethod.POST,
                httpJsonEntity(body),
                Void.class)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw 404 when adding operation for not existing element"() {
        given:
        def transactionId = beginTransaction(spaceName).body

        def element = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .id(null)
                .build()
        // creates bucket implicitly
        addElement(spaceName, element)

        def notExistingElement = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .build()

        def operation = OperationTestBuilder.builder()
                .type(Operation.OperationType.UPDATE)
                .element(notExistingElement)
                .build()

        when:
        addOperation(spaceName, transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND

    }

    def "should throw 404 when adding operation for not existing bucket"() {
        given:
        def transactionId = beginTransaction(spaceName).body

        def element = ElementTestBuilder.builder().build()
        def operation = OperationTestBuilder.builder()
            .type(Operation.OperationType.READ)
            .element(element)
            .build()

        when:
        addOperation(spaceName, transactionId, operation)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should add other than create operation to transaction"() {
        given:
        def transactionId = beginTransaction(spaceName).body

        def elementToCreate = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .id(null)
                .build()

        def elementId = addElement(spaceName, elementToCreate).body.id
        def elementToUpdate = ElementTestBuilder.builder()
                .bucketName(TEST_BUCKET_NAME)
                .id(elementId)
                .build()

        def operation = OperationTestBuilder.builder()
                .type(Operation.OperationType.UPDATE)
                .element(elementToUpdate)
                .build()

        when:
        ResponseEntity<Void> response = addOperation(spaceName, transactionId, operation)

        then:
        response.statusCode == HttpStatus.CREATED
        repository.get(transactionId).operations.size() == 1
    }
}
