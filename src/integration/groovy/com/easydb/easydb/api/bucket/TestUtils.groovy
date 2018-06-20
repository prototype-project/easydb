package com.easydb.easydb.api.bucket

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.api.PaginatedElementsApiDto
import groovy.json.JsonOutput
import groovy.transform.SelfType
import org.springframework.http.ResponseEntity

@SelfType(BaseIntegrationSpec)
trait TestUtils {

    static String TEST_BUCKET_NAME = "peoples"

    PaginatedElementsApiDto filterElements(String fullUrl) {
        restTemplate.getForEntity(
                fullUrl,
                PaginatedElementsApiDto.class).body
    }

    PaginatedElementsApiDto filterElements(String spaceName, int offset, int limit, Map<String, String> filters = [:]) {
        String filtersAsString = ''
        filters.forEach({ key, val -> filtersAsString = filtersAsString + '&' + key + '=' + val })
        filterElements(buildUrl(spaceName, TEST_BUCKET_NAME, limit, offset, filtersAsString))
    }

    String buildUrl(String spaceId, String bucketName, int limit, int offset, String filters = '') {
        localUrl(String.format("/api/v1/%s/%s?limit=%d&offset=%d&%s", spaceId, bucketName, limit, offset, filters))
    }

    ResponseEntity<ElementQueryApiDto> addSampleElement(String spaceName, String body) {
        addSampleElement(spaceName, TEST_BUCKET_NAME, body)
    }


    ResponseEntity<ElementQueryApiDto> addSampleElement(String spaceName) {
        addSampleElement(spaceName, TEST_BUCKET_NAME, sampleElement())
    }

    String buildElementBody(String firstName, String lastName) {
        JsonOutput.toJson([
                fields: [
                        [
                                name: 'firstName',
                                value: firstName
                        ],
                        [
                                name: 'lastName',
                                value: lastName
                        ]
                ]
        ])
    }

    def sampleElement() {
        buildElementBody("john", "smith")
    }

    def sampleElementUpdate() {
        buildElementBody("john", "snow")
    }
}
