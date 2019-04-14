package com.easydb.easydb

import com.easydb.easydb.domain.BucketName
import com.easydb.easydb.domain.bucket.BucketDoesNotExistException
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.transactions.BucketRepository
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDoesNotExistException
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.SpaceRemovalService
import org.springframework.beans.factory.annotation.Autowired

class IntegrationWithCleanedDatabaseSpec extends BaseIntegrationSpec {
    public static final TEST_BUCKET_NAME = new BucketName("testSpace","testBucket")

    @Autowired
    SpaceRepository spaceRepository

    @Autowired
    SpaceRemovalService spaceRemovalService

    @Autowired
    BucketRepository bucketRepository

    @Autowired
    BucketService bucketService

    def setup() {
        try {
            spaceRemovalService.remove(TEST_BUCKET_NAME.getSpaceName())
        } catch (SpaceDoesNotExistException | BucketDoesNotExistException ignored ) {}
        spaceRepository.save(Space.of(TEST_BUCKET_NAME.getSpaceName()))
    }

    def cleanup() {
        spaceRemovalService.remove(TEST_BUCKET_NAME.getSpaceName())
    }
}
