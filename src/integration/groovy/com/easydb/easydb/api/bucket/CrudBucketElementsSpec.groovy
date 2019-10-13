package com.easydb.easydb.api.bucket

import com.easydb.easydb.ApiIntegrationWithAutoCreatedSpace
import com.easydb.easydb.ElementCrudDtoTestBuilder
import com.easydb.easydb.ElementUtils
import com.easydb.easydb.domain.bucket.BucketName
import com.easydb.easydb.domain.bucket.BucketQuery
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.space.SpaceRemovalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

import java.util.stream.Collectors

class CrudBucketElementsSpec extends ApiIntegrationWithAutoCreatedSpace implements ElementUtils {

    @Autowired
    SpaceRemovalService spaceRemovalService

    @Autowired
    BucketService bucketService

    String someForSureExistingElementId

    def setup() {
        createTestBucket(spaceName)
        someForSureExistingElementId = addElement(spaceName, TEST_BUCKET_NAME,
                ElementCrudDtoTestBuilder.builder().build()).body.id
    }

    def "should add element to bucket"() {
        when:
        ResponseEntity<ElementQueryDto> response = addSampleElementToTestBucket(spaceName)

        then:
        response.statusCode == HttpStatus.CREATED

        and:
        response.body == ElementQueryDto.of(bucketService.getElement(new BucketName(spaceName, TEST_BUCKET_NAME), response.body.getId()))
    }

    def "should add element to bucket with custom id"() {
        when:
        ResponseEntity<ElementQueryDto> response = addElement(spaceName, TEST_BUCKET_NAME,
                ElementCrudDtoTestBuilder
                        .builder()
                        .addId("CustomId")
                        .build())

        then:
        response.statusCode == HttpStatus.CREATED

        and:
        response.body == ElementQueryDto.of(bucketService.getElement(new BucketName(spaceName, TEST_BUCKET_NAME), "CustomId"))
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
        !bucketService.elementExists(new BucketName(spaceName, TEST_BUCKET_NAME), addElementResponse.body.getId())
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
        Element updatedElement = bucketService.getElement(new BucketName(spaceName, TEST_BUCKET_NAME), addElementResponse.body.getId())
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
        BucketQuery query = BucketQuery.of(new BucketName(spaceName, TEST_BUCKET_NAME), 20, 0)
        response.body.results == bucketService.filterElements(query).stream()
                .map({ it -> ElementQueryDto.of(it) })
                .collect(Collectors.toList())
    }

    def "should get element from bucket"() {
        given:
        ResponseEntity<ElementQueryDto> addElementResponse = addSampleElementToTestBucket(spaceName)

        when:
        ResponseEntity<ElementQueryDto> getElementResponse = getElement(spaceName, TEST_BUCKET_NAME, addElementResponse.body.getId())

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
        ex.responseBodyAsString =~ "BUCKET_DOES_NOT_EXIST"
    }

    def "should return 404 when trying to add element to nonexistent space"() {
        when:
        addSampleElementToTestBucket("nonExistentSpace")

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.statusCode == HttpStatus.NOT_FOUND
        ex.responseBodyAsString =~ "SPACE_DOES_NOT_EXIST"
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
        ex.responseBodyAsString =~ "BUCKET_DOES_NOT_EXIST"
    }

    def "should return 404 when trying to update element in nonexistent space"() {
        when:
        restTemplate.exchange(
                buildElementUrl("nonExistentSpace", TEST_BUCKET_NAME, someForSureExistingElementId),
                HttpMethod.PUT,
                httpJsonEntity(sampleUpdateElementBody()),
                Void.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.statusCode == HttpStatus.NOT_FOUND
        ex.responseBodyAsString =~ "SPACE_DOES_NOT_EXIST"
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
        when:
        restTemplate.exchange(
                buildElementUrl(spaceName, "nonExistentBucket", someForSureExistingElementId),
                HttpMethod.GET,
                null,
                ElementQueryDto.class)

        then:
        def response = thrown(HttpClientErrorException)

        and:
        response.statusCode == HttpStatus.NOT_FOUND
        response.responseBodyAsString =~ "BUCKET_DOES_NOT_EXIST"
    }

    def "should return 404 when trying to get element from nonexistent space"() {
        when:
        restTemplate.exchange(
                buildElementUrl("nonExistentSpace", TEST_BUCKET_NAME, someForSureExistingElementId),
                HttpMethod.GET,
                null,
                ElementQueryDto.class)

        then:
        def response = thrown(HttpClientErrorException)

        and:
        response.statusCode == HttpStatus.NOT_FOUND
        response.responseBodyAsString =~ "SPACE_DOES_NOT_EXIST"
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
        def response = thrown(HttpClientErrorException)

        and:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should throw 400 when trying to add element to bucket with existing id"() {
        def crudDto = ElementCrudDtoTestBuilder
                .builder()
                .addId("CustomId")
                .build()
        when:
        ResponseEntity<ElementQueryDto> responseCreated = addElement(spaceName, TEST_BUCKET_NAME, crudDto)

        then:
        responseCreated.statusCode == HttpStatus.CREATED

        when:
        addElement(spaceName, TEST_BUCKET_NAME, crudDto)

        then:
        def responseAlreadyExists = thrown(HttpClientErrorException)
        responseAlreadyExists.statusCode == HttpStatus.BAD_REQUEST
        responseAlreadyExists.responseBodyAsString =~ 'ELEMENT_ALREADY_EXISTS'
    }
}
