package com.easydb.easydb

import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.domain.ElementQueryDto
import com.easydb.easydb.domain.Space
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration

import java.util.stream.Collectors

@ContextConfiguration(classes = [SpaceTestConfig])
class CrudBucketSpec extends BaseSpec {
    @Autowired
    Space space

    def setup() {
        space.createBucket('people')
    }

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
