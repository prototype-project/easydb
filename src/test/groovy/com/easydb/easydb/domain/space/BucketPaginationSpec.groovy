package com.easydb.easydb.domain.space

import com.easydb.easydb.domain.ElementTestBuilder
import com.easydb.easydb.domain.bucket.BucketQuery
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.bucket.InvalidPaginationDataException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll;

class BucketPaginationSpec extends Specification {

    public static final String TEST_BUCKET_NAME = "people"
    public static final String TEST_SPACE = "testSpace"

    @Shared
    Space space

    def setupSpec() {
        SpaceDefinition spaceDefinition = SpaceDefinition.of(TEST_SPACE)
        SpaceTestConfig.SPACE_DEFINITION_REPOSITORY.save(spaceDefinition)
        space = SpaceTestConfig.SPACE_FACTORY.buildSpace(spaceDefinition)
    }

    def cleanupSpec() {
        SpaceTestConfig.SPACE_DEFINITION_REPOSITORY.remove(TEST_SPACE)
    }

    def cleanup() {
        space.removeBucket(TEST_BUCKET_NAME)
    }

    @Unroll
    def "should paginate elements"() {
        given:
        createElements()
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, limit, offset)

        when:
        List<Element> elements = space.filterElements(query)

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
        List<Element> elements = space.filterElements(query)

        then:
        elements.size() == 0

    }

    @Unroll
    def "should throw error when limit is <= 0"() {
        when:
        createElements()
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, limit, 2)

        and:
        space.filterElements(query)

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
        space.filterElements(query)

        then:
        thrown(InvalidPaginationDataException)
    }

    def createElements() {
        space.addElement(ElementTestBuilder.builder().fields([ElementField.of('firstName', 'John')]).build())
        space.addElement(ElementTestBuilder.builder().fields([ElementField.of('firstName', 'Anna')]).build())
        space.addElement(ElementTestBuilder.builder().fields([ElementField.of('firstName', 'Maria')]).build())
    }
}
