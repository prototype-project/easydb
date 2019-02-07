package com.easydb.easydb.infrastructure.bucket

import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.domain.bucket.*
import spock.lang.Unroll

class BucketServicePaginationSpec extends IntegrationWithCleanedDatabaseSpec {

    @Unroll
    def "should paginate elements"() {
        given:
        createElements()
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, limit, offset)

        when:
        List<Element> elements = bucketService.filterElements(query)

        then:
        elements.size() == expectedNumberOfElements

        where:
        limit | offset | expectedNumberOfElements

        1     | 1      | 1
        1     | 0      | 1
        2     | 0      | 2
        3     | 0      | 3
        3     | 1      | 2
        3     | 2      | 1
        3     | 3      | 0
        3     | 5      | 0
    }

    def "should return empty list when there is no elements"() {
        given:
        bucketService.createBucket(TEST_BUCKET_NAME)
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, 1, 0)

        when:
        List<Element> elements = bucketService.filterElements(query)

        then:
        elements.size() == 0
    }

    @Unroll
    def "should throw error when limit is <= 0"() {
        when:
        createElements()
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, limit, 2)

        and:
        bucketService.filterElements(query)

        then:
        thrown(InvalidPaginationDataException)

        where:
        limit << [-1, 0]
    }

    def "should throw error when offset is < 0"() {
        when:
        createElements()
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, 1, -2)

        and:
        bucketService.filterElements(query)

        then:
        thrown(InvalidPaginationDataException)
    }

    def createElements() {
        bucketService.createBucket(TEST_BUCKET_NAME)
        bucketService.addElement(ElementTestBuilder.builder().bucketName(TEST_BUCKET_NAME).fields([ElementField.of('firstName', 'John')]).build())
        bucketService.addElement(ElementTestBuilder.builder().bucketName(TEST_BUCKET_NAME).fields([ElementField.of('firstName', 'Anna')]).build())
        bucketService.addElement(ElementTestBuilder.builder().bucketName(TEST_BUCKET_NAME).fields([ElementField.of('firstName', 'Maria')]).build())
    }
}
