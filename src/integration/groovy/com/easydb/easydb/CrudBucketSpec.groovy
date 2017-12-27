package com.easydb.easydb

import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.domain.bucket.dto.ElementQueryDto
import com.easydb.easydb.domain.Space
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

import java.util.stream.Collectors

class CrudBucketSpec extends BaseSpec {
    @Autowired
    Space space

    def cleanup() {
        space.removeBucket('people')
    }

    def "should add element to bucket"() {
        when:
        ResponseEntity<ElementQueryApiDto> response = addSampleElement()

        then:
        response.statusCodeValue == 201

        and:
        response.body == ElementQueryApiDto.from(space.getElement('people', response.body.getId()))

        and:
        ElementQueryApiDto.from(space.getElement('people', response.body.getId())) == response.body
    }

    def "should remove element from bucket"() {
        given:
        ResponseEntity<ElementQueryApiDto> addElementResponse = addSampleElement()

        when:
        ResponseEntity deleteElementResponse = restTemplate.exchange(
                localUrl('/api/v1/buckets/people/' + addElementResponse.body.getId()),
                HttpMethod.DELETE,
                null,
                Void.class)

        then:
        deleteElementResponse.statusCodeValue == 200

        and:
        !space.elementExists('people', addElementResponse.body.getId())
    }

    def "should update element"() {
        given:
        ResponseEntity<ElementQueryApiDto> addElementResponse = addSampleElement()

        when:
        ResponseEntity response = restTemplate.exchange(
                localUrl('/api/v1/buckets/people/' + addElementResponse.body.getId()),
                HttpMethod.PUT,
                httpJsonEntity(sampleElementUpdate(addElementResponse.body.getId())),
                Void.class)

        then:
        response.statusCodeValue == 200

        and:
        ElementQueryDto updatedElement = space.getElement('people', addElementResponse.body.getId())
        updatedElement.getFieldValue('firstName') == 'john'
        updatedElement.getFieldValue('lastName') == 'snow'
    }

    def "should get all elements from bucket"() {
        given:
        ResponseEntity<ElementQueryApiDto> addElementResponse = addSampleElement()

        when:
        ResponseEntity<List<ElementQueryApiDto>> response = restTemplate.exchange(
                localUrl('/api/v1/buckets/people'),
                HttpMethod.GET,
                null,
                ElementQueryApiDto[].class)

        then:
        response.body == space.getAllElements('people').stream()
                .map({it -> ElementQueryApiDto.from(it)})
                .collect(Collectors.toList())
    }

    def "should get element from bucket"() {
        given:
        ResponseEntity<ElementQueryApiDto> addElementResponse = addSampleElement()

        when:
        ResponseEntity<ElementQueryApiDto> getElementResponse = restTemplate.exchange(
                localUrl('/api/v1/buckets/people/' + addElementResponse.body.getId()),
                HttpMethod.GET,
                null,
                ElementQueryApiDto.class)

        then:
        addElementResponse.body == getElementResponse.body
    }

    def "should return 404 when trying to update element in nonexistent bucket"() {
        when:
        restTemplate.exchange(
                localUrl('/api/v1/buckets/people/' + 'someId'),
                HttpMethod.PUT,
                httpJsonEntity(sampleElementUpdate('someId')),
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
                localUrl('/api/v1/buckets/people/' + 'nonexistentId'),
                HttpMethod.PUT,
                httpJsonEntity(sampleElementUpdate('nonexistentId')),
                Void.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }

    def "should return 404 when trying to get element from nonexistent bucket"() {
        when:
        restTemplate.exchange(
                localUrl('/api/v1/buckets/people/' + 'someId'),
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
                localUrl('/api/v1/buckets/people/' + 'nonexistentId'),
                HttpMethod.GET,
                null,
                ElementQueryApiDto.class)

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }

    ResponseEntity<ElementQueryApiDto> addSampleElement() {
        restTemplate.exchange(
                localUrl('/api/v1/buckets/people'),
                HttpMethod.POST,
                httpJsonEntity(sampleElement()),
                ElementQueryApiDto.class)
    }

    // make is simpler in next sprint
    def sampleElement() {
        JsonOutput.toJson([
                bucketName: 'people',
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

    def sampleElementUpdate(String elementId) {
        JsonOutput.toJson([
                id: elementId,
                bucketName: 'people',
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
