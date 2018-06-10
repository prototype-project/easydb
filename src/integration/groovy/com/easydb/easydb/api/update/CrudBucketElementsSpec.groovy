package com.easydb.easydb.api.update

import com.easydb.easydb.BaseSpec
import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.api.PaginatedElementsApiDto
import com.easydb.easydb.domain.bucket.BucketQuery
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.SpaceServiceFactory
import com.easydb.easydb.domain.space.SpaceService
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

import java.util.stream.Collectors

class CrudBucketElementsSpec extends BaseSpec {

    @Autowired
    SpaceServiceFactory spaceFactory

    @Autowired
    SpaceRepository spaceDefinitionRepository

    SpaceService space

    String TEST_SPACE_NAME = "testSpace"
    String TEST_BUCKET_NAME = "testBucket"

    def setup() {
        Space spaceDefinition = Space.of(TEST_SPACE_NAME)
        spaceDefinitionRepository.save(spaceDefinition)
        space = spaceFactory.buildSpaceService(spaceDefinition)
    }

    def cleanup() {
        space.removeBucket(TEST_BUCKET_NAME)
        spaceDefinitionRepository.remove(TEST_SPACE_NAME) // TODO remove all buckets when removing definition
    }

    def "should add element to bucket"() {
        when:
        ResponseEntity<ElementQueryApiDto> response = addSampleElement()

        then:
        response.statusCodeValue == 201

        and:
        response.body == ElementQueryApiDto.from(space.getElement(TEST_BUCKET_NAME, response.body.getId()))
    }

    def "should remove element from bucket"() {
        given:
        ResponseEntity<ElementQueryApiDto> addElementResponse = addSampleElement()

        when:
        ResponseEntity deleteElementResponse = restTemplate.exchange(
                localUrl('/api/v1/' + TEST_SPACE_NAME + '/'+ TEST_BUCKET_NAME + '/' + addElementResponse.body.getId()),
                HttpMethod.DELETE,
                null,
                Void.class)

        then:
        deleteElementResponse.statusCodeValue == 200

        and:
        !space.elementExists(TEST_BUCKET_NAME, addElementResponse.body.getId())
    }

    def "should update element"() {
        given:
        ResponseEntity<ElementQueryApiDto> addElementResponse = addSampleElement()

        when:
        ResponseEntity response = restTemplate.exchange(
                localUrl('/api/v1/' + TEST_SPACE_NAME + '/'+ TEST_BUCKET_NAME + '/' + addElementResponse.body.getId()),
                HttpMethod.PUT,
                httpJsonEntity(sampleElementUpdate()),
                Void.class)

        then:
        response.statusCodeValue == 200

        and:
        Element updatedElement = space.getElement(TEST_BUCKET_NAME, addElementResponse.body.getId())
        updatedElement.getFieldValue('firstName') == 'john'
        updatedElement.getFieldValue('lastName') == 'snow'
    }

    def "should get all elements from bucket"() {
        given:
        addSampleElement()

        when:
        ResponseEntity<List<ElementQueryApiDto>> response = restTemplate.exchange(
                localUrl('/api/v1/' + TEST_SPACE_NAME + '/'+ TEST_BUCKET_NAME),
                HttpMethod.GET,
                null,
                PaginatedElementsApiDto.class)

        then:
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, 20, 0)
        response.body.results == space.filterElements(query).stream()
                .map({it -> ElementQueryApiDto.from(it)})
                .collect(Collectors.toList())
    }

    def "should get element from bucket"() {
        given:
        ResponseEntity<ElementQueryApiDto> addElementResponse = addSampleElement()

        when:
        ResponseEntity<ElementQueryApiDto> getElementResponse = restTemplate.exchange(
                localUrl('/api/v1/' + TEST_SPACE_NAME + '/'+ TEST_BUCKET_NAME + '/' + addElementResponse.body.getId()),
                HttpMethod.GET,
                null,
                ElementQueryApiDto.class)

        then:
        addElementResponse.body == getElementResponse.body
    }

    def "should return 404 when trying to update element in nonexistent bucket"() {
        when:
        restTemplate.exchange(
                localUrl('/api/v1/' + TEST_SPACE_NAME + '/'+ TEST_BUCKET_NAME + '/' + 'someId'),
                HttpMethod.PUT,
                httpJsonEntity(sampleElementUpdate()),
                Void.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }

    def "should return 404 when trying to update nonexistent element"() {
        given:
        addSampleElement()

        when:
        restTemplate.exchange(
                localUrl('/api/v1/' + TEST_SPACE_NAME + '/'+ TEST_BUCKET_NAME + '/' + 'nonexistentId'),
                HttpMethod.PUT,
                httpJsonEntity(sampleElementUpdate()),
                Void.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }

    def "should return 404 when trying to get element from nonexistent bucket"() {
        when:
        restTemplate.exchange(
                localUrl('/api/v1/' + TEST_SPACE_NAME + '/'+ TEST_BUCKET_NAME + '/' + 'someId'),
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
        addSampleElement()

        when:
        restTemplate.exchange(
                localUrl('/api/v1/' + TEST_SPACE_NAME + '/'+ TEST_BUCKET_NAME + '/' + 'nonexistentId'),
                HttpMethod.GET,
                null,
                ElementQueryApiDto.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }

    private ResponseEntity<ElementQueryApiDto> addSampleElement() {
        addSampleElement(TEST_SPACE_NAME, TEST_BUCKET_NAME, sampleElement())
    }

    private static def sampleElement() {
        JsonOutput.toJson([
                fields: [
                        [
                                name: 'firstName',
                                value: 'john'
                        ],
                        [
                                name: 'lastName',
                                value: 'smith'
                        ]
                ]
        ])
    }

    private static def sampleElementUpdate() {
        JsonOutput.toJson([
                fields: [
                        [
                                name: 'firstName',
                                value: 'john'
                        ],
                        [
                                name: 'lastName',
                                value: 'snow'
                        ]
                ]
        ])
    }
}
