package com.easydb.easydb.api.bucket

import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.IntegrationDatabaseSpec
import com.easydb.easydb.TestHttpOperations
import com.easydb.easydb.domain.bucket.BucketName
import com.easydb.easydb.domain.bucket.ElementField
import org.springframework.web.client.HttpClientErrorException;

class BucketElementsPaginationSpec extends IntegrationDatabaseSpec implements TestHttpOperations {

    private String spaceName

    def setup() {
        spaceName = addSampleSpace().body.spaceName
        createTestBucket(spaceName)

        def testBucketName = new BucketName(spaceName, TEST_BUCKET_NAME)
        addElementToTestBucket(spaceName, buildElementBody(
                ElementTestBuilder
                        .builder()
                        .bucketName(testBucketName)
                        .clearFields()
                        .addField(ElementField.of("firstName", "Daniel"))
                        .addField(ElementField.of("lastName", "Faderski"))
                        .build()))

        addElementToTestBucket(spaceName, buildElementBody(
                ElementTestBuilder
                        .builder()
                        .bucketName(testBucketName)
                        .clearFields()
                        .addField(ElementField.of("firstName", "Daniel"))
                        .addField(ElementField.of("lastName", "Faderski"))
                        .build()))

        addElementToTestBucket(spaceName, buildElementBody(
                ElementTestBuilder
                        .builder()
                        .bucketName(testBucketName)
                        .clearFields()
                        .addField(ElementField.of("firstName", "Daniel"))
                        .addField(ElementField.of("lastName", "Faderski"))
                        .build()))
    }

    def "should properly paginate results by offset when all elements was fetched"() {
        when:
        PaginatedElementsDto paginated = getElementsFromTestBucket(spaceName, 1, 2)

        then:
        paginated.getResults().size() == 2
        paginated.getNext() == null
    }

    def "should properly paginate results by offset when there are still more elements to fetch"() {
        when:
        PaginatedElementsDto paginated = getElementsFromTestBucket(spaceName, 0, 1)

        then:
        paginated.getResults().size() == 1
        paginated.getNext().contains("?limit=1&offset=1")

        when:
        paginated = getElementsByFullUrl(paginated.getNext())

        then:
        paginated.results.size() == 1
        paginated.next.contains("?limit=1&offset=2")

        when:
        paginated = getElementsByFullUrl(paginated.getNext())

        then:
        paginated.results.size() == 1
        paginated.next == null
    }

    def "should properly paginate results by limit when all elements was fetched"() {
        when:
        PaginatedElementsDto paginated = getElementsFromTestBucket(spaceName, 0, 4)

        then:
        paginated.getResults().size() == 3
        paginated.getNext() == null
    }

    def "should properly paginate results by limit when there are still more elements to fetch"() {
        when:
        PaginatedElementsDto paginated = getElementsFromTestBucket(spaceName, 0, 2)

        then:
        paginated.getResults().size() == 2
        paginated.getNext().contains("?limit=2&offset=2")

        when:
        paginated = getElementsByFullUrl(paginated.getNext())

        then:
        paginated.getResults().size() == 1
        paginated.getNext() == null
    }

    def "should throw error when trying to paginate by limit <= 0"() {
        when:
        getElementsFromTestBucket(spaceName, 0, 0)

        then:
        def response = thrown(HttpClientErrorException)
        response.rawStatusCode == 400
    }

    def "should throw error when trying to paginate by offset < 0"() {
        when:
        getElementsFromTestBucket(spaceName, -1, 2)

        then:
        def response = thrown(HttpClientErrorException)
        response.rawStatusCode == 400
    }
}
