package com.easydb.easydb.domain.space

import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.domain.bucket.BucketQuery
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.bucket.InvalidPaginationDataException
import spock.lang.Shared
import spock.lang.Unroll

class BucketPaginationSpec extends BaseSpecification {

    public static final String TEST_BUCKET_NAME = "people"
    public static final String TEST_SPACE = "testSpace"

    @Shared
    SpaceService spaceService

    def setupSpec() {
        Space space = Space.of(TEST_SPACE)
        spaceRepository.save(space)
        spaceService = spaceFactory.buildSpaceService(space)
    }

    def cleanupSpec() {
        spaceRepository.remove(TEST_SPACE)
    }

    def cleanup() {
        spaceService.removeBucket(TEST_BUCKET_NAME)
    }

    @Unroll
    def "should paginate elements"() {
        given:
        createElements()
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, limit, offset)

        when:
        List<Element> elements = spaceService.filterElements(query)

        then:
        elements.size() == expectedNumberOfElements

        where:
        limit | offset | expectedNumberOfElements

        1     |   1    |   1
        1     |   0    |   1
        2     |   0    |   2
        3     |   0    |   3
        3     |   1    |   2
        3     |   2    |   1
        3     |   3    |   0
        3     |   5    |   0
    }

    def "should return empty list when there is no elements"() {
        given:
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, 1, 0)

        when:
        List<Element> elements = spaceService.filterElements(query)

        then:
        elements.size() == 0

    }

    @Unroll
    def "should throw error when limit is <= 0"() {
        when:
        createElements()
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, limit, 2)

        and:
        spaceService.filterElements(query)

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
        spaceService.filterElements(query)

        then:
        thrown(InvalidPaginationDataException)
    }

    def createElements() {
        spaceService.addElement(ElementTestBuilder.builder().fields([ElementField.of('firstName', 'John')]).build())
        spaceService.addElement(ElementTestBuilder.builder().fields([ElementField.of('firstName', 'Anna')]).build())
        spaceService.addElement(ElementTestBuilder.builder().fields([ElementField.of('firstName', 'Maria')]).build())
    }
}
