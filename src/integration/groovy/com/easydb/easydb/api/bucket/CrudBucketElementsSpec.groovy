package com.easydb.easydb.api.bucket

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.TestHttpOperations
import com.easydb.easydb.api.ElementQueryDto
import com.easydb.easydb.api.PaginatedElementsDto
import com.easydb.easydb.domain.bucket.BucketQuery
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.space.SpaceRemovalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

import java.util.stream.Collectors

import static com.easydb.easydb.infrastructure.bucket.graphql.Query.DEFAULT_GRAPHQL_QUERY

class CrudBucketElementsSpec extends BaseIntegrationSpec implements TestHttpOperations {

    @Autowired
    SpaceRemovalService spaceRemovalService

    @Autowired
    BucketServiceFactory bucketServiceFactory

    String spaceName
    BucketService bucketService
    String someForSureExistingElementId

    def setup() {
        spaceName = addSampleSpace().body.spaceName
        this.bucketService = bucketServiceFactory.buildBucketService(spaceName)
        createTestBucket(spaceName)
        someForSureExistingElementId = addElement(spaceName,
                ElementTestBuilder
                        .builder()
                        .bucketName(TEST_BUCKET_NAME)
                        .build()).body.id
    }

    def "should add element to bucket"() {
        when:
        ResponseEntity<ElementQueryDto> response = addSampleElementToTestBucket(spaceName)

        then:
        response.statusCode == HttpStatus.CREATED

        and:
        response.body == ElementQueryDto.of(bucketService.getElement(TEST_BUCKET_NAME, response.body.getId()))
    }

    def "should remove element from bucket"() {
        given:
        ResponseEntity<ElementQueryDto> addElementResponse = addSampleElementToTestBucket(spaceName)

        when:
        ResponseEntity deleteElementResponse = restTemplate.exchange(
                buildElementUrl(spaceName, TEST_BUCKET_NAME, addElementResponse.body.getId()),
                HttpMethod.DELETE,
                null,
                Void.class)

        then:
        deleteElementResponse.statusCode == HttpStatus.OK

        and:
        !bucketService.elementExists(TEST_BUCKET_NAME, addElementResponse.body.getId())
    }

    def "should update element"() {
        given:
        ResponseEntity<ElementQueryDto> addElementResponse = addSampleElementToTestBucket(spaceName)

        when:
        ResponseEntity response = restTemplate.exchange(
                buildElementUrl(spaceName, TEST_BUCKET_NAME, addElementResponse.body.getId()),
                HttpMethod.PUT,
                httpJsonEntity(sampleUpdateElementBody()),
                Void.class)

        then:
        response.statusCode == HttpStatus.OK

        and:
        Element updatedElement = bucketService.getElement(TEST_BUCKET_NAME, addElementResponse.body.getId())
        getFieldValue(updatedElement, 'firstName') == 'john'
        getFieldValue(updatedElement, 'lastName') == 'snow'
    }

    def "should get all elements from bucket"() {
        given:
        addSampleElementToTestBucket(spaceName)

        when:
        ResponseEntity<List<ElementQueryDto>> response = restTemplate.exchange(
                buildElementUrl(spaceName, TEST_BUCKET_NAME),
                HttpMethod.GET,
                null,
                PaginatedElementsDto.class)

        then:
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, 20, 0)
        response.body.results == bucketService.filterElements(query).stream()
                .map({ it -> ElementQueryDto.of(it) })
                .collect(Collectors.toList())
    }

    def "should get element from bucket"() {
        given:
        ResponseEntity<ElementQueryDto> addElementResponse = addSampleElementToTestBucket(spaceName)

        when:
        ResponseEntity<ElementQueryDto> getElementResponse = restTemplate.exchange(
                buildElementUrl(spaceName, TEST_BUCKET_NAME, addElementResponse.body.getId()),
                HttpMethod.GET,
                null,
                ElementQueryDto.class)

        then:
        addElementResponse.body == getElementResponse.body
    }

    def "should return 404 when trying to add element to nonexistent bucket"() {
        when:
        restTemplate.exchange(
                buildElementUrl(spaceName, "notExistentBucket"),
                HttpMethod.POST,
                httpJsonEntity(sampleUpdateElementBody()),
                Void.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.statusCode == HttpStatus.NOT_FOUND
    }

    def "should return 404 when trying to update element in nonexistent bucket"() {
        deleteTestBucket(spaceName)

        when:
        restTemplate.exchange(
                buildElementUrl(spaceName, TEST_BUCKET_NAME, someForSureExistingElementId),
                HttpMethod.PUT,
                httpJsonEntity(sampleUpdateElementBody()),
                Void.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.statusCode == HttpStatus.NOT_FOUND
    }

    def "should return 404 when trying to update nonexistent element"() {
        given:
        addSampleElementToTestBucket(spaceName)

        when:
        restTemplate.exchange(
                buildElementUrl(spaceName, TEST_BUCKET_NAME, "notExistingId"),
                HttpMethod.PUT,
                httpJsonEntity(sampleUpdateElementBody()),
                Void.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }

    def "should return 404 when trying to get element from nonexistent bucket"() {
        given:
        deleteTestBucket(spaceName)

        when:
        restTemplate.exchange(
                buildElementUrl(spaceName, TEST_BUCKET_NAME, someForSureExistingElementId),
                HttpMethod.GET,
                null,
                ElementQueryDto.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }

    def "should return 404 when trying to get nonexistent element"() {
        given:
        addSampleElementToTestBucket(spaceName)

        when:
        restTemplate.exchange(
                buildElementUrl(spaceName, TEST_BUCKET_NAME, "notExistingId"),
                HttpMethod.GET,
                null,
                ElementQueryDto.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }

    static getFieldValue(Element element, String fieldName) {
        return element.fields.stream()
                .filter({f -> f.getName() == fieldName})
                .map({it.value}).findFirst().get()
    }
}
