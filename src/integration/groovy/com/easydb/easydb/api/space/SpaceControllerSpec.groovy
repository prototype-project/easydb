package com.easydb.easydb.api.space

import com.easydb.easydb.IntegrationDatabaseSpec
import com.easydb.easydb.TestHttpOperations
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException


class SpaceControllerSpec extends IntegrationDatabaseSpec implements TestHttpOperations {

    def "should create new space"() {
        when:
        ResponseEntity<SpaceDefinitionCreateDto> response = addSampleSpace()

        then:
        response.statusCode == HttpStatus.CREATED
        with(response.body) {
            spaceName != null
        }
    }

    def "should remove space"() {
        given:
        String spaceName = addSampleSpace().getBody().spaceName

        when:
        restTemplate.delete(buildSpaceUrl(spaceName))

        and:
        restTemplate.getForEntity(localUrl("/api/v1/spaces/" + spaceName), SpaceDefinitionCreateDto)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should get space"() {
        given:
        String spaceName = addSampleSpace().getBody().spaceName
        createBucket(spaceName, "testBucket")

        when:
        ResponseEntity<SpaceDetailsDto> response = restTemplate.getForEntity(
                buildSpaceUrl(spaceName), SpaceDetailsDto)

        then:
        response.statusCode == HttpStatus.OK

        response.body.buckets == ["testBucket"]
    }

    def "should return 404 when get not existing space"() {
        when:
        restTemplate.getForEntity(
                buildSpaceUrl("notExisting"), SpaceDefinitionCreateDto)

        then:
        def response = thrown(HttpClientErrorException)
        response.statusCode == HttpStatus.NOT_FOUND
    }
}
