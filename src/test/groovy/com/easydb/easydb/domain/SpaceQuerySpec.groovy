package com.easydb.easydb.domain

import com.easydb.easydb.domain.bucket.BucketQuery
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.bucket.InvalidPaginationDataException
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDefinition
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll;

class SpaceQuerySpec extends Specification {

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

    def "should limit number of elements"() {
        when:
        createElements()
        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, 1, 1)

        then:
        List<Element> elements = space.filterElements(query)
        elements.size() == 1
        elements[0].getFieldValue("firstName") == "Anna"
    }

    @Unroll
    def "should return proper number of elements based on limit"() {
        when:
        createElements()

        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, limit, 0)

        then:
        space.filterElements(query).size() == limit

        where:
        limit << [1, 2, 3]
    }

    @Unroll
    def "should return proper number of elements based on offset"() {
        when:
        createElements()

        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, 3, offset)

        then:
        space.filterElements(query).size() == 3 - offset

        where:
        offset << [0, 1, 2, 3]
    }

    def "should return empty list when offset is to bigger than number of elements"() {
        when:
        createElements()

        BucketQuery query = BucketQuery.of(TEST_BUCKET_NAME, 3, 5)

        then:
        space.filterElements(query).size() == 0
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
