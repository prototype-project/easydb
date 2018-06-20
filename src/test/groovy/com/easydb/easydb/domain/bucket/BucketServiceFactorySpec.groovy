package com.easydb.easydb.domain.bucket

import com.easydb.easydb.BaseSpecification
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDoesNotExistException


class BucketServiceFactorySpec extends BaseSpecification {

    def cleanup() {
        try {
            spaceRepository.remove("testSpace")
        } catch (Exception ignored) {}
    }

    def "should build space"() {
        when:
        spaceRepository.save(Space.of("testSpace"))
        BucketService space = bucketServiceFactory.buildBucketService("testSpace")

        then:
        space != null
    }

    def "should throw error when building bucket service for not existing space"() {
        when:
        bucketServiceFactory.buildBucketService("notExistingSpace")

        then:
        thrown(SpaceDoesNotExistException)
    }
}