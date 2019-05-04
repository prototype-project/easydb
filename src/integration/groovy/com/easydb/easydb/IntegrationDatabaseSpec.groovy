package com.easydb.easydb

import com.easydb.easydb.domain.bucket.BucketName
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.transactions.BucketRepository
import com.easydb.easydb.domain.space.SpaceRemovalService
import com.easydb.easydb.domain.space.SpaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = SpaceTestConfig)
class IntegrationDatabaseSpec extends BaseIntegrationSpec {
    public static TEST_BUCKET_NAME = new BucketName("testSpace","testBucket")

    @Autowired
    SpaceRepository spaceRepository

    @Autowired
    SpaceRemovalService spaceRemovalService

    @Autowired
    BucketRepository bucketRepository

    @Autowired
    BucketService bucketService

    @Autowired
    MongoTemplate mongoTemplate
}
