package com.easydb.easydb.infrastructure.space

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.domain.bucket.BucketRepository
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.BucketServiceFactory
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDoesNotExistException
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.SpaceService
import org.springframework.beans.factory.annotation.Autowired


class SpaceServiceSpec extends BaseIntegrationSpec {

    public static final String TEST_SPACE = "testSpace"

    @Autowired
    SpaceService spaceService

    @Autowired SpaceRepository spaceRepository

    @Autowired BucketRepository bucketRepository

    @Autowired
    BucketServiceFactory bucketServiceFactory

    def cleanup() {
        try {
            spaceService.remove(TEST_SPACE)
        } catch (SpaceDoesNotExistException ignored) {}
    }

    def "should remove all space's buckets when removing space"() {
        given:
        spaceRepository.save(Space.of(TEST_SPACE))
        BucketService bucketService = bucketServiceFactory.buildBucketService(TEST_SPACE)
        Element sampleElement = ElementTestBuilder.builder().build()
        bucketService.addElement(sampleElement)

        when:
        spaceService.remove(TEST_SPACE)

        then:
        !spaceRepository.exists(TEST_SPACE)
        !bucketService.bucketExists(sampleElement.bucketName)
    }
}
