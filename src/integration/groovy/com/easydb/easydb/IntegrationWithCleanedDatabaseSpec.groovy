package com.easydb.easydb

import com.easydb.easydb.domain.bucket.BucketDoesNotExistException
import com.easydb.easydb.domain.bucket.BucketRepository
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.BucketServiceFactory
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDoesNotExistException
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.SpaceService
import org.springframework.beans.factory.annotation.Autowired

class IntegrationWithCleanedDatabaseSpec extends BaseIntegrationSpec {
    static final String TEST_BUCKET_NAME = "testBucket"
    static final String TEST_SPACE = "testSpace"

    BucketService bucketService

    @Autowired
    SpaceRepository spaceRepository

    @Autowired
    SpaceService spaceService

    @Autowired
    BucketRepository bucketRepository

    @Autowired
    BucketServiceFactory bucketServiceFactory

    def setup() {
        try {
            spaceService.remove(TEST_SPACE)
        } catch (SpaceDoesNotExistException | BucketDoesNotExistException ignored ) {}
        spaceRepository.save(Space.of(TEST_SPACE))
        bucketService = bucketServiceFactory.buildBucketService(TEST_SPACE)
    }

    def cleanup() {
        spaceService.remove(TEST_SPACE)
    }
}
