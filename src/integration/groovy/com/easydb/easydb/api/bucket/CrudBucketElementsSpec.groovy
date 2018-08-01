package com.easydb.easydb.api.bucket

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.TestUtils
import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.api.PaginatedElementsApiDto
import com.easydb.easydb.domain.bucket.BucketQuery
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.BucketServiceFactory
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.space.SpaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

import java.util.stream.Collectors

class CrudBucketElementsSpec extends BaseIntegrationSpec implements TestUtils {

    @Autowired
    SpaceService spaceService

    @Autowired
    BucketServiceFactory bucketServiceFactory

    String spaceName
    BucketService bucketService

    def setup() {
        spaceName = addSampleSpace().body.spaceName
        this.bucketService = bucketServiceFactory.buildBucketService(spaceName)
    }

    def "should add element to bucket"() {
        when:
        ResponseEntity<ElementQueryApiDto> response = addSampleElement(spaceName)

        then:
        response.statusCodeValue == 201

        and:
        response.body == ElementQueryApiDto.from(bucketService.getElement(TEST_BUCKET_NAME, response.body.getId()))
    }

    def "should remove element from bucket"() {
        given:
        ResponseEntity<ElementQueryApiDto> addElementResponse = addSampleElement(spaceName)

        when:
        ResponseEntity deleteElementResponse = restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/'+ TEST_BUCKET_NAME + '/' + addElementResponse.body.getId()),
                HttpMethod.DELETE,
                null,
                Void.class)

        then:
        deleteElementResponse.statusCodeValue == 200

        and:
        !bucketService.elementExists(TEST_BUCKET_NAME, addElementResponse.body.getId())
    }

    def "should update element"() {
        given:
        ResponseEntity<ElementQueryApiDto> addElementResponse = addSampleElement(spaceName)

        when:
        ResponseEntity response = restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/'+ TEST_BUCKET_NAME + '/' + addElementResponse.body.getId()),
                HttpMethod.PUT,
                httpJsonEntity(sampleUpdateElementBody()),
                Void.class)

        then:
        response.statusCodeValue == 200

        and:
        Element updatedElement = bucketService.getElement(TEST_BUCKET_NAME, addElementResponse.body.getId())
        updatedElement.getFieldValue('firstName') == 'john'
        updatedElement.getFieldValue('lastName') == 'snow'
    }

    def "should get all elements from bucket"() {
        given:
        addSampleElement(spaceName)

        when:
        ResponseEntity<List<ElementQueryApiDto>> response = restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/'+ TEST_BUCKET_NAME),
                HttpMethod.GET,
                null,
                PaginatedElementsApiDto.class)

        then:
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, 20, 0)
        response.body.results == bucketService.filterElements(query).stream()
                .map({it -> ElementQueryApiDto.from(it)})
                .collect(Collectors.toList())
    }

    def "should get element from bucket"() {
        given:
        ResponseEntity<ElementQueryApiDto> addElementResponse = addSampleElement(spaceName)

        when:
        ResponseEntity<ElementQueryApiDto> getElementResponse = restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/'+ TEST_BUCKET_NAME + '/' + addElementResponse.body.getId()),
                HttpMethod.GET,
                null,
                ElementQueryApiDto.class)

        then:
        addElementResponse.body == getElementResponse.body
    }

    def "should return 404 when trying to update element in nonexistent bucket"() {
        when:
        restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/'+ TEST_BUCKET_NAME + '/' + 'someId'),
                HttpMethod.PUT,
                httpJsonEntity(sampleUpdateElementBody()),
                Void.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }

    def "should return 404 when trying to update nonexistent element"() {
        given:
        addSampleElement(spaceName)

        when:
        restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/'+ TEST_BUCKET_NAME + '/' + 'nonexistentId'),
                HttpMethod.PUT,
                httpJsonEntity(sampleUpdateElementBody()),
                Void.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }

    def "should return 404 when trying to get element from nonexistent bucket"() {
        when:
        restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/' + TEST_BUCKET_NAME + '/' + 'someId'),
                HttpMethod.GET,
                null,
                ElementQueryApiDto.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }

    def "should return 404 when trying to get nonexistent element"() {
        given:
        addSampleElement(spaceName)

        when:
        restTemplate.exchange(
                localUrl('/api/v1/' + spaceName + '/'+ TEST_BUCKET_NAME + '/' + 'nonexistentId'),
                HttpMethod.GET,
                null,
                ElementQueryApiDto.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }
}
