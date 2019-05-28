package com.easydb.easydb

import com.easydb.easydb.domain.bucket.BucketDoesNotExistException
import com.easydb.easydb.domain.bucket.BucketName
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDoesNotExistException

class IntegrationWithCleanedDatabaseSpec extends IntegrationDatabaseSpec {

    public static TEST_BUCKET_NAME = new BucketName("testSpace", "testBucket")

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
