package com.easydb.easydb

import com.easydb.easydb.domain.bucket.BucketDoesNotExistException
import com.easydb.easydb.domain.bucket.transactions.BucketRepository
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDoesNotExistException
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.SpaceRemovalService
import org.springframework.beans.factory.annotation.Autowired

class IntegrationWithCleanedDatabaseSpec extends BaseIntegrationSpec {
    public static final String TEST_BUCKET_NAME = "testBucket"
    public static final String TEST_SPACE = "testSpace"

    BucketService bucketService

    @Autowired
    SpaceRepository spaceRepository

    @Autowired
    SpaceRemovalService spaceRemovalService

    @Autowired
    BucketRepository bucketRepository

    @Autowired
    BucketServiceFactory bucketServiceFactory

    def setup() {
        try {
            spaceRemovalService.remove(TEST_SPACE)
        } catch (SpaceDoesNotExistException | BucketDoesNotExistException ignored ) {}
        spaceRepository.save(Space.of(TEST_SPACE))
        bucketService = bucketServiceFactory.buildBucketService(TEST_SPACE)
    }

    def cleanup() {
        spaceRemovalService.remove(TEST_SPACE)
    }
}
