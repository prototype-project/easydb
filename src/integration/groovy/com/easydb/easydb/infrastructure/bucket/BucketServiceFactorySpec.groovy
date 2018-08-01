package com.easydb.easydb.infrastructure.bucket

import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.BucketServiceFactory
import com.easydb.easydb.domain.space.SpaceDoesNotExistException
import com.easydb.easydb.domain.space.SpaceRepository
import org.springframework.beans.factory.annotation.Autowired


class BucketServiceFactorySpec extends IntegrationWithCleanedDatabaseSpec {

    @Autowired
    SpaceRepository spaceRepository

    @Autowired
    BucketServiceFactory bucketServiceFactory

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