package com.easydb.easydb.api.bucket

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.TestHttpOperations
import com.easydb.easydb.api.PaginatedElementsDto
import com.easydb.easydb.domain.bucket.ElementField


class FilterBucketElementsSpec extends BaseIntegrationSpec implements TestHttpOperations {

    private String spaceName

    def setup() {
        spaceName = addSampleSpace().body.spaceName
        createTestBucket(spaceName)
        addElementToTestBucket(spaceName, buildElementBody(
                ElementTestBuilder
                        .builder()
                        .bucketName(TEST_BUCKET_NAME)
                        .clearFields()
                        .addField(ElementField.of("firstName", "Daniel"))
                        .addField(ElementField.of("lastName", "Faderski"))
                        .build()))

        addElementToTestBucket(spaceName, buildElementBody(
                ElementTestBuilder
                        .builder()
                        .bucketName(TEST_BUCKET_NAME)
                        .clearFields()
                        .addField(ElementField.of("firstName", "Jan"))
                        .addField(ElementField.of("lastName", "Brzechwa"))
                        .build()))

        addElementToTestBucket(spaceName, buildElementBody(
                ElementTestBuilder
                        .builder()
                        .bucketName(TEST_BUCKET_NAME)
                        .clearFields()
                        .addField(ElementField.of("firstName", "Jurek"))
                        .addField(ElementField.of("lastName", "Ogórek"))
                        .build()))
    }

    def "should filter elements by field equality"() {
        given:
        String query = """
        {
            elements(filter: {
                fieldsFilters: [
                    {
                        name: "siema"
                        value: "eniu"
                    }
                ]
                or: [
                        {
                            fieldsFilters: [
                                {
                                    name: "inny"
                                    value: "inny"
                                }
                            ]
                        }
                    ]
            }) {
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
        {}
    }


}
