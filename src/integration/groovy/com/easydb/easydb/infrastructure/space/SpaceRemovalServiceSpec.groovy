package com.easydb.easydb.infrastructure.space

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.domain.bucket.BucketRepository
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDoesNotExistException
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.SpaceRemovalService
import org.springframework.beans.factory.annotation.Autowired


class SpaceRemovalServiceSpec extends BaseIntegrationSpec {

    @Autowired
    SpaceRemovalService spaceRemovalService

    @Autowired SpaceRepository spaceRepository

    @Autowired BucketRepository bucketRepository

    @Autowired
    BucketServiceFactory bucketServiceFactory

    def "should remove all spaces buckets when removing space"() {
        given:
        spaceRepository.save(Space.of("testSpace"))
        BucketService bucketService = bucketServiceFactory.buildBucketService("testSpace")
        bucketService.createBucket("testBucket")

        when:
        spaceRemovalService.remove("testSpace")

        then:
        !spaceRepository.exists("testSpace")
        !bucketService.bucketExists("testBucket")
    }
}
