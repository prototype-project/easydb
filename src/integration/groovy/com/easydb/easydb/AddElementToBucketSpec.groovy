package com.easydb.easydb

import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.domain.Space
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [SpaceTestConfig])
class AddElementToBucketSpec extends BaseSpec {
    @Autowired
    Space space

    def setup() {
        space.createBucket('people', ['firstName', 'lastName'])
    }

    def "should add element to bucket"() {
        when:
        ResponseEntity<ElementQueryApiDto> response = restTemplate.exchange(
                localUrl('/api/v1/buckets/people'),
                HttpMethod.POST,
                httpJsonEntity(sampleElement()),
                ElementQueryApiDto.class)

        then:
        response.statusCodeValue == 201

        and:
        response.body == ElementQueryApiDto.from(space.getElement('people', response.body.getId()))

        and:
        ElementQueryApiDto.from(space.getElement('people', response.body.getId())) == response.body
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
}
