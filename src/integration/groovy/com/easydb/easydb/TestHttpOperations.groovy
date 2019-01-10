package com.easydb.easydb

import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.api.OperationResultDto
import com.easydb.easydb.api.PaginatedElementsApiDto
import com.easydb.easydb.api.SpaceDefinitionApiDto
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

    PaginatedElementsApiDto getElements(String spaceName, int offset=0, int limit=10, Map<String, String> filters = [:]) {
        String filtersAsString = ''
        filters.forEach({ key, val -> filtersAsString = filtersAsString + '&' + key + '=' + val })
        getElementsByFullUrl(buildUrl(spaceName, TEST_BUCKET_NAME, limit, offset, filtersAsString))
    }

    PaginatedElementsApiDto getElementsByFullUrl(String fullUrl) {
        restTemplate.getForEntity(
                fullUrl,
                PaginatedElementsApiDto.class).body
    }

    ElementQueryApiDto getElement(String spaceName, String bucketName, String id) {
        restTemplate.getForEntity(
                localUrl("/api/v1/$spaceName/$bucketName/$id"), ElementQueryApiDto).body
    }

    String buildUrl(String spaceId, String bucketName, int limit, int offset, String filters = '') {
        localUrl(String.format("/api/v1/%s/%s?limit=%d&offset=%d&%s", spaceId, bucketName, limit, offset, filters))
    }

    ResponseEntity<ElementQueryApiDto> addElement(String spaceName, String bucketName, String body) {
        return restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/'+ bucketName),
                HttpMethod.POST,
                httpJsonEntity(body),
                ElementQueryApiDto.class)
    }

    ResponseEntity<ElementQueryApiDto> addElement(String spaceName, String body) {
        addElement(spaceName, TEST_BUCKET_NAME, body)
    }

    ResponseEntity<ElementQueryApiDto> addSampleElement(String spaceName) {
        addElement(spaceName, TEST_BUCKET_NAME, sampleElementBody())
    }

    ResponseEntity<ElementQueryApiDto> addElement(String spaceName, Element element) {
        return restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/'+ element.bucketName),
                HttpMethod.POST,
                httpJsonEntity(buildElementBody(element)),
                ElementQueryApiDto.class)
    }

    String sampleElementBody() {
        buildElementBody(
                ElementTestBuilder.builder().build())
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

    ResponseEntity<SpaceDefinitionApiDto> addSampleSpace() {
        return restTemplate.postForEntity(
                localUrl("/api/v1/spaces/"),
                Void,
                SpaceDefinitionApiDto.class)
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
                fields: element.fields.collect {["name": it.name, "value": it.value]}
        ])
    }

    String buildOperationBody(Operation operation) {
        JsonOutput.toJson([
                type: operation.type,
                fields: operation.fields.collect {["name": it.name, "value": it.value]},
                bucketName: operation.bucketName,
                elementId: operation.elementId
        ])
    }
}
