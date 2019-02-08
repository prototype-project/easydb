package com.easydb.easydb

import com.easydb.easydb.api.ElementQueryDto
import com.easydb.easydb.api.OperationResultDto
import com.easydb.easydb.api.PaginatedElementsDto
import com.easydb.easydb.api.SpaceDefinitionDto
import com.easydb.easydb.api.TransactionDto
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.transactions.Operation
import groovy.json.JsonOutput
import groovy.transform.SelfType
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

@SelfType(BaseIntegrationSpec)
trait TestHttpOperations {

    static String TEST_BUCKET_NAME = "bucketPeoples"

    PaginatedElementsDto getElementsFromTestBucket(String spaceName, int offset = 0, int limit = 10, Map<String, String> filters = [:]) {
        String filtersAsString = ''
        filters.forEach({ key, val -> filtersAsString = filtersAsString + '&' + key + '=' + val })
        getElementsByFullUrl(buildUrl(spaceName, TEST_BUCKET_NAME, limit, offset, filtersAsString))
    }

    PaginatedElementsDto getElementsByFullUrl(String fullUrl) {
        restTemplate.getForEntity(
                fullUrl,
                PaginatedElementsDto.class).body
    }

    ElementQueryDto getElement(String spaceName, String bucketName, String id) {
        restTemplate.getForEntity(
                buildElementUrl(spaceName, bucketName, id), ElementQueryDto).body
    }

    ResponseEntity<ElementQueryDto> addElementToTestBucket(String spaceName, String bucketName, String body) {
        return restTemplate.exchange(
                buildElementUrl(spaceName, bucketName),
                HttpMethod.POST,
                httpJsonEntity(body),
                ElementQueryDto.class)
    }

    ResponseEntity<ElementQueryDto> addElementToTestBucket(String spaceName, String body) {
        addElementToTestBucket(spaceName, TEST_BUCKET_NAME, body)
    }

    ResponseEntity<ElementQueryDto> addSampleElementToTestBucket(String spaceName) {
        addElementToTestBucket(spaceName, TEST_BUCKET_NAME, sampleElementBody())
    }

    ResponseEntity<ElementQueryDto> addElement(String spaceName, Element element) {
        return restTemplate.exchange(
                buildElementUrl(spaceName, element.bucketName),
                HttpMethod.POST,
                httpJsonEntity(buildElementBody(element)),
                ElementQueryDto.class)
    }

    def createTestBucket(String spaceName) {
        createBucket(spaceName, TEST_BUCKET_NAME)
    }

    ResponseEntity<Void> createBucket(String spaceName, String bucketName) {
        return restTemplate.exchange(
                buildBucketUrl(spaceName),
                HttpMethod.POST,
                httpJsonEntity(buildBucketBody(bucketName)),
                Void.class
        )
    }

    def deleteTestBucket(String spaceName) {
        deleteBucket(spaceName, TEST_BUCKET_NAME)
    }

    def deleteBucket(String spaceName, String bucketName) {
        restTemplate.delete(buildBucketUrl(spaceName, bucketName))
    }

    String sampleUpdateElementBody() {
        buildElementBody(
                ElementTestBuilder
                        .builder()
                        .clearFields()
                        .addField(ElementField.of("firstName", "john"))
                        .addField(ElementField.of("lastName", "snow"))
                        .build())
    }

    ResponseEntity<SpaceDefinitionDto> addSampleSpace() {
        return restTemplate.postForEntity(
                buildSpaceUrl(),
                Void,
                SpaceDefinitionDto.class)
    }

    ResponseEntity<TransactionDto> beginTransaction(String spaceName) {
        return restTemplate.postForEntity(
                localUrl("/api/v1/transactions/${spaceName}"),
                Void, TransactionDto
        )
    }

    ResponseEntity<OperationResultDto> addOperation(String transactionId, Operation operation) {
        return restTemplate.exchange(
                localUrl("/api/v1/transactions/${transactionId}/add-operation"),
                HttpMethod.POST,
                httpJsonEntity(buildOperationBody(operation)),
                OperationResultDto.class
        )
    }

    ResponseEntity<Void> commitTransaction(String transactionId) {
        return restTemplate.postForEntity(
                localUrl("/api/v1/transactions/${transactionId}/commit"),
                Void, Void
        )
    }

    String buildElementBody(Element element) {
        JsonOutput.toJson([
                fields: element.fields.collect { ["name": it.name, "value": it.value] }
        ])
    }

    String buildOperationBody(Operation operation) {
        JsonOutput.toJson([
                type      : operation.type,
                fields    : operation.fields.collect { ["name": it.name, "value": it.value] },
                bucketName: operation.bucketName,
                elementId : operation.elementId
        ])
    }

    String buildSpaceUrl(String spaceName = "") {
        return localUrl("/api/v1/spaces/${spaceName}")
    }

    String buildBucketUrl(String spaceName, String bucketName = "") {
        return buildSpaceUrl(spaceName) + "/buckets/${bucketName}"
    }

    String buildElementUrl(String spaceName, String bucketName, String elementId = "") {
        return buildBucketUrl(spaceName, bucketName) + "/elements/${elementId}"
    }

    String buildBucketBody(String bucketName) {
        JsonOutput.toJson([
                bucketName: bucketName
        ])
    }

    private String sampleElementBody() {
        buildElementBody(
                ElementTestBuilder.builder().build())
    }

    private String buildUrl(String spaceId, String bucketName, int limit, int offset, String filters = '') {
        localUrl(String.format("/api/v1/spaces/%s/buckets/%s/elements?limit=%d&offset=%d&%s", spaceId, bucketName, limit, offset, filters))
    }
}
