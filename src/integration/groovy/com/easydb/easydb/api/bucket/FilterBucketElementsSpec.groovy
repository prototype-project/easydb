package com.easydb.easydb.api.bucket

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.TestHttpOperations

class FilterBucketElementsSpec extends BaseIntegrationSpec implements TestHttpOperations {

//    private String spaceName
//
//    def setup() {
//        spaceName = addSampleSpace().body.spaceName
//        addElementToTestBucket(spaceName, buildElementBody("Daniel", "D"))
//        addElementToTestBucket(spaceName, buildElementBody("Bartek", "B"))
//        addElementToTestBucket(spaceName, buildElementBody("Zdzisiek", "Z1"))
//        addElementToTestBucket(spaceName, buildElementBody("Zdzisiek", "Z2"))
//        addElementToTestBucket(spaceName, buildElementBody("Zdzisiek", "Z2", '20'))
//        addElementToTestBucket(spaceName, buildElementBody("Zdzisiek", "Z2", '20'))
//        addElementToTestBucket(spaceName, buildElementBody("Zdzisiek", "Z2", '21'))
//    }
//
//    def "should filter com.easydb.easydb.element.bucket elements by single field"() {
//        when:
//        PaginatedElementsDto filteredElements = getElementsByFullUrl(spaceName, 0, 7, [firstName: firstName])
//
//        then:
//        filteredElements.results.size() == expectedSize
//
//        where:
//        firstName  | expectedSize
//        "Zdzisiek" | 5
//        "Bartek"   | 1
//        "Daniel"   | 1
//        "Smith"    | 0
//        "@#%!#%@"  | 0
//    }
//
//    def "should filter com.easydb.easydb.element.bucket elements by multiple fields"() {
//        given:
//        def filters = [
//                firstName: firstName,
//                lastName : lastName
//        ]
//        if (age != null) {
//            filters.age = age
//        }
//
//        when:
//        PaginatedElementsDto filteredElements = getElementsByFullUrl(spaceName, 0, 7, filters)
//
//        then:
//        filteredElements.results.size() == expectedSize
//
//        where:
//        firstName  | lastName | age  | expectedSize
//        "Zdzisiek" | "Z1"     | null | 1
//        "Bartek"   | "B"      | null | 1
//        "Smith"    | "S"      | null | 0
//        "Zdzisiek" | "Z2"     | '21' | 1
//        "Zdzisiek" | "Z2"     | '20' | 2
//        "Zdzisiek" | "Z1"     | '20' | 0
//    }
//
//    def "should not ignore unknown filter fields"() {
//        given:
//        def filters = [
//                firstName: 'Zdzisiek',
//                uknownField : 'someValue'
//        ]
//
//        when:
//        PaginatedElementsDto filteredElements = getElementsByFullUrl(spaceName, 0, 7, filters)
//
//        then:
//        filteredElements.results.size() == 0
//    }
//
//    String buildElementBody(String firstName, String lastName, String age = null) {
//        def result = [
//                fields: [
//                        [
//                                name : "firstName",
//                                value: firstName
//                        ],
//                        [
//                                name : "lastName",
//                                value: lastName
//                        ]
//                ]
//        ]
//        if (age != null) {
//            result.fields.add([name: 'age', value: age])
//        }
//
//        JsonOutput.toJson(result)
//    }
}
