package com.easydb.easydb.infrastructure.space

import com.easydb.easydb.IntegrationDatabaseSpec
import com.easydb.easydb.domain.bucket.BucketName
import com.easydb.easydb.domain.bucket.transactions.BucketRepository
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.SpaceRemovalService
import org.springframework.beans.factory.annotation.Autowired


class SpaceRemovalServiceSpec extends IntegrationDatabaseSpec {

    @Autowired
    SpaceRemovalService spaceRemovalService

    @Autowired SpaceRepository spaceRepository

    @Autowired BucketRepository bucketRepository

    @Autowired
    BucketService bucketService

    def "should remove all spaces buckets when removing space"() {
        given:
        spaceRepository.save(Space.of("testSpace"))
        bucketService.createBucket(new BucketName("testSpace","testBucket"))

        when:
        spaceRemovalService.remove("testSpace")

        then:
        !spaceRepository.exists("testSpace")
        !bucketService.bucketExists(new BucketName("testSpace", "testBucket"))
    }
}
