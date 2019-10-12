package com.easydb.easydb

import com.easydb.easydb.api.bucket.ElementCrudDto
import com.easydb.easydb.api.bucket.ElementFieldDto
import com.easydb.easydb.api.bucket.ElementQueryDto
import com.easydb.easydb.api.transaction.OperationDto
import com.easydb.easydb.api.transaction.OperationResultDto
import com.easydb.easydb.api.bucket.PaginatedElementsDto
import com.easydb.easydb.api.space.SpaceDefinitionCreateDto
import com.easydb.easydb.api.transaction.TransactionDto
import groovy.json.JsonOutput
import groovy.transform.SelfType
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.yaml.snakeyaml.util.UriEncoder

@SelfType(BaseIntegrationSpec)
trait TestHttpOperations {

    static String TEST_BUCKET_NAME = "bucketPeoples"

    PaginatedElementsDto getElementsFromTestBucket(String spaceName, int offset = 0, int limit = 10, String query = '') {
        getElementsByFullUrl(buildUrl(spaceName, TEST_BUCKET_NAME, limit, offset, query))
    }

    PaginatedElementsDto getElementsByFullUrl(String fullUrl) {
        restTemplate.getForObject(
                fullUrl,
                PaginatedElementsDto.class)
    }

    ResponseEntity<ElementQueryDto> getElement(String spaceName, String bucketName, String id) {
        restTemplate.getForEntity(
                buildElementUrl(spaceName, bucketName, id), ElementQueryDto)
    }

    ResponseEntity<ElementQueryDto> addElementToTestBucket(String spaceName, String body) {
        addElementToTestBucket(spaceName, TEST_BUCKET_NAME, body)
    }

    ResponseEntity<ElementQueryDto> addSampleElementToTestBucket(String spaceName) {
        addElementToTestBucket(spaceName, TEST_BUCKET_NAME, sampleElementBody())
    }

    ResponseEntity<ElementQueryDto> addElementToTestBucket(String spaceName, String bucketName, String body) {
        return restTemplate.exchange(
                buildElementUrl(spaceName, bucketName),
                HttpMethod.POST,
                httpJsonEntity(body),
                ElementQueryDto.class)
    }

    ResponseEntity<ElementQueryDto> addElement(String spaceName, String bucketName, ElementCrudDto element) {
        return restTemplate.exchange(
                buildElementUrl(spaceName, bucketName),
                HttpMethod.POST,
                httpJsonEntity(buildElementBody(element)),
                ElementQueryDto.class)
    }

    ResponseEntity<Void> createTestBucket(String spaceName) {
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

    void deleteTestBucket(String spaceName) {
        deleteBucket(spaceName, TEST_BUCKET_NAME)
    }

    void deleteBucket(String spaceName, String bucketName) {
        restTemplate.delete(buildBucketUrl(spaceName, bucketName))
    }

    String sampleUpdateElementBody() {
        buildElementBody(
                ElementCrudDtoTestBuilder
                        .builder()
                        .clearFields()
                        .addField(new ElementFieldDto("firstName", "john"))
                        .addField(new ElementFieldDto("lastName", "snow"))
                        .build())
    }

    ResponseEntity<SpaceDefinitionCreateDto> addSampleSpace() {
        return restTemplate.postForEntity(
                buildSpaceUrl(),
                Void,
                SpaceDefinitionCreateDto.class)
    }

    ResponseEntity<TransactionDto> beginTransaction(String spaceName) {
        return restTemplate.postForEntity(
                localUrl("/api/v1/spaces/${spaceName}/transactions/"),
                Void, TransactionDto
        )
    }

    ResponseEntity<OperationResultDto> addOperation(String spaceName, String transactionId, OperationDto operation) {
        return restTemplate.exchange(
                localUrl("/api/v1/spaces/${spaceName}/transactions/${transactionId}/add-operation"),
                HttpMethod.POST,
                httpJsonEntity(buildOperationBody(operation)),
                OperationResultDto.class
        )
    }

    ResponseEntity<Void> commitTransaction(String spaceName, String transactionId) {
        return restTemplate.postForEntity(
                localUrl("/api/v1/spaces/${spaceName}/transactions/${transactionId}/commit"),
                Void, Void
        )
    }

    String buildElementBody(ElementCrudDto element) {
        Map<String, Object> bodyAsMap = [fields: element.fields.collect { ["name": it.name, "value": it.value] }]
        element.id.ifPresent({ id -> bodyAsMap.put("id", id) })
        JsonOutput.toJson(bodyAsMap)
    }

    String buildOperationBody(OperationDto operation) {
        JsonOutput.toJson([
                type      : operation.type,
                fields    : operation.fields.collect { ["name": it.name, "value": it.value] },
                bucketName: operation.bucketName,
                elementId : operation.elementId
        ])
    }

    String buildElementUrl(String spaceName, String bucketName, String elementId = "") {
        return buildBucketUrl(spaceName, bucketName) + "/elements/${elementId}"
    }

    String buildBucketUrl(String spaceName, String bucketName = "") {
        return buildSpaceUrl(spaceName) + "/buckets/${bucketName}"
    }

    String buildSpaceUrl(String spaceName = "") {
        return localUrl("/api/v1/spaces/${spaceName}")
    }

    String buildBucketBody(String bucketName) {
        JsonOutput.toJson([
                bucketName: bucketName
        ])
    }

    private String sampleElementBody() {
        buildElementBody(ElementCrudDtoTestBuilder.builder().build())
    }

    private String buildUrl(String spaceId, String bucketName, int limit, int offset, String query) {
        String queryString = ''
        if (query) {
            queryString = '&query=' + UriEncoder.encode(query)
        }
        localUrl(String.format("/api/v1/spaces/%s/buckets/%s/elements?limit=%d&offset=%d%s", spaceId, bucketName, limit, offset, queryString))
    }
}
