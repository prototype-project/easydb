package com.easydb.easydb.api.query

import com.easydb.easydb.BaseSpec
import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.api.PaginatedElementsApiDto
import groovy.transform.SelfType
import org.springframework.http.ResponseEntity

@SelfType(BaseSpec)
trait QueryUtils {
    static String TEST_BUCKET_NAME = "people"

    PaginatedElementsApiDto filterElements(String fullUrl) {
        restTemplate.getForEntity(
                fullUrl,
                PaginatedElementsApiDto.class).body
    }

    ResponseEntity<ElementQueryApiDto> addSampleElement(String spaceName, String body) {
        addSampleElement(spaceName, TEST_BUCKET_NAME, body)
    }

    PaginatedElementsApiDto filterElements(String spaceName, int offset, int limit, Map<String, String> filters = [:]) {
        String filtersAsString = ''
        filters.forEach({ key, val -> filtersAsString = filtersAsString + '&' + key + '=' + val })
        filterElements(buildUrl(spaceName, TEST_BUCKET_NAME, limit, offset, filtersAsString))
    }

    String buildUrl(String spaceId, String bucketName, int limit, int offset, String filters = '') {
        localUrl(String.format("/api/v1/%s/%s?limit=%d&offset=%d&%s", spaceId, bucketName, limit, offset, filters))
    }
}
