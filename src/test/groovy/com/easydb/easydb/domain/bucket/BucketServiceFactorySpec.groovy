package com.easydb.easydb.domain.bucket

import com.easydb.easydb.BaseSpecification
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDoesNotExistException


class BucketServiceFactorySpec extends BaseSpecification {

    static TEST_SPACE = "testSpace"

    def setupSpec() {
        spaceService.save(Space.of(TEST_SPACE))
    }

    def cleanupSpec() {
        spaceService.remove(TEST_SPACE)
    }

    def "should build bucket service"() {
        when:
        BucketService bucketService = bucketServiceFactory.buildBucketService(TEST_SPACE)

        then:
        bucketService != null
    }

    def "should throw error when building bucket service for not existing space"() {
        when:
        bucketServiceFactory.buildBucketService("notExistingSpace")

        then:
        thrown(SpaceDoesNotExistException)
    }
}