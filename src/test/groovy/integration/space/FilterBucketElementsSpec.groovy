package integration.space

import integration.BaseSpec
import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.api.PaginatedElementsApiDto
import com.easydb.easydb.api.SpaceDefinitionApiDto
import groovy.json.JsonOutput
import org.springframework.http.ResponseEntity


class FilterBucketElementsSpec extends BaseSpec {
    String BUCKET_NAME = "people"

    ResponseEntity<ElementQueryApiDto> addSampleElement(String spaceName, String body) {
        addSampleElement(spaceName, BUCKET_NAME, body)
    }

    String buildElementBody(String firstName, String lastName, String age = null) {
        def result = [
                fields: [
                        [
                                name : "firstName",
                                value: firstName
                        ],
                        [
                                name : "lastName",
                                value: lastName
                        ]
                ]
        ]
        if (age != null) {
            result.fields.add([name: 'age', value: age])
        }

        JsonOutput.toJson(result)
    }

    private String spaceName

    protected ResponseEntity<SpaceDefinitionApiDto> addSampleSpace() {
        return restTemplate.postForEntity(
                localUrl("/api/v1/spaces/"),
                Void,
                SpaceDefinitionApiDto.class)
    }

    def setup() {
        spaceName = addSampleSpace().body.spaceName
        addSampleElement(spaceName, buildElementBody("Daniel", "D"))
        addSampleElement(spaceName, buildElementBody("Bartek", "B"))
        addSampleElement(spaceName, buildElementBody("Zdzisiek", "Z1"))
        addSampleElement(spaceName, buildElementBody("Zdzisiek", "Z2"))
        addSampleElement(spaceName, buildElementBody("Zdzisiek", "Z2", '20'))
        addSampleElement(spaceName, buildElementBody("Zdzisiek", "Z2", '20'))
        addSampleElement(spaceName, buildElementBody("Zdzisiek", "Z2", '21'))
    }

    def "should filter bucket elements by single field"() {
        when:
        PaginatedElementsApiDto filteredElements = filterElements(spaceName, 0, 4, [firstName: firstName])

        then:
        filteredElements.results.size() == expectedSize

        where:
        firstName  | expectedSize
        "Zdzisiek" | 5
        "Bartek"   | 1
        "Daniel"   | 1
        "Smith"    | 0
    }

    def "should filter bucket elements by multiple fields"() {
        given:
        def filters = [
                firstName: firstName,
                lastName : lastName
        ]
        if (age != null) {
            filters.age = age
        }

        when:
        PaginatedElementsApiDto filteredElements = filterElements(spaceName, 0, 4, filters)

        then:
        filteredElements.results.size() == expectedSize

        where:
        firstName  | lastName | age  | expectedSize
        "Zdzisiek" | "Z1"     | null | 1
        "Bartek"   | "B"      | null | 1
        "Smith"    | "S"      | null | 0
        "Zdzisiek" | "Z2"     | '21' | 1
        "Zdzisiek" | "Z2"     | '20' | 2
        "Zdzisiek" | "Z1"     | '20' | 0
    }

    PaginatedElementsApiDto filterElements(String spaceName, int offset, int limit, Map<String, String> filters) {
        String filtersAsString = ''
        filters.forEach({ key, val -> filtersAsString = filtersAsString + '&' + key + '=' + val })
        filterElements(buildUrl(spaceName, BUCKET_NAME, limit, offset, filtersAsString))
    }

    String buildUrl(String spaceId, String bucketName, int limit, int offset, String filters) {
        localUrl(String.format("/api/v1/%s/%s?limit=%d&offset=%d&%s", spaceId, bucketName, limit, offset, filters))
    }

    PaginatedElementsApiDto filterElements(String fullUrl) {
        restTemplate.getForEntity(
                fullUrl,
                PaginatedElementsApiDto.class).body
    }
}
