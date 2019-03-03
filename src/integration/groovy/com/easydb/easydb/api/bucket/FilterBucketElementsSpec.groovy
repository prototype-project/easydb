package com.easydb.easydb.api.bucket

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.TestHttpOperations
import com.easydb.easydb.api.PaginatedElementsDto
import com.easydb.easydb.domain.bucket.ElementField


class FilterBucketElementsSpec extends BaseIntegrationSpec implements TestHttpOperations {

    private String spaceName

    def danielFaderski
    def janBrzechwa
    def jurekOgorek

    def setup() {
        spaceName = addSampleSpace().body.spaceName
        createTestBucket(spaceName)

        danielFaderski = addElementToTestBucket(spaceName, buildElementBody(
                ElementTestBuilder
                        .builder()
                        .bucketName(TEST_BUCKET_NAME)
                        .clearFields()
                        .addField(ElementField.of("firstName", "Daniel"))
                        .addField(ElementField.of("lastName", "Faderski"))
                        .build())).getBody()

        janBrzechwa = addElementToTestBucket(spaceName, buildElementBody(
                ElementTestBuilder
                        .builder()
                        .bucketName(TEST_BUCKET_NAME)
                        .clearFields()
                        .addField(ElementField.of("firstName", "Jan"))
                        .addField(ElementField.of("lastName", "Brzechwa"))
                        .build())).getBody()

        jurekOgorek = addElementToTestBucket(spaceName, buildElementBody(
                ElementTestBuilder
                        .builder()
                        .bucketName(TEST_BUCKET_NAME)
                        .clearFields()
                        .addField(ElementField.of("firstName", "Jurek"))
                        .addField(ElementField.of("lastName", "Ogórek"))
                        .build())).getBody()
    }

    def "should filter elements by field equality"() {
        given: "given query to find all people with lastName 'Faderski' or name 'Jan'"
        String query = """
        {
            elements(filter: {
                or: [
                        {
                            fieldsFilters: [
                                {
                                    name: "firstName"
                                    value: "Jan"
                                }
                            ]
                        },
                        {
                            fieldsFilters: [
                                {
                                    name: "lastName"
                                    value: "Faderski"
                                }
                            ]
                        }
                    ]
            }) {
                id
                fields {
                    name 
                    value
                }
            }
        }
        """

        when:
        PaginatedElementsDto elements = getElementsFromTestBucket(spaceName, 0, 20, query)

        then:
        elements.results.size() == 2
        elements.results as Set == [janBrzechwa, danielFaderski] as Set
    }


}
