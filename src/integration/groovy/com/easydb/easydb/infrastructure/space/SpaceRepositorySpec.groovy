package com.easydb.easydb.infrastructure.space

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDoesNotExistException
import com.easydb.easydb.domain.space.SpaceNameNotUniqueException
import com.easydb.easydb.domain.space.SpaceRepository
import org.springframework.beans.factory.annotation.Autowired


class SpaceRepositorySpec extends BaseIntegrationSpec {

    String TEST_SPACE = "repositoryTestSpace"

    @Autowired
    SpaceRepository spaceRepository

    @Autowired
    BucketServiceFactory bucketServiceFactory

    def cleanup() {
        try {
            spaceRepository.remove(TEST_SPACE)
        } catch (SpaceDoesNotExistException ignored) {}
    }

    def "should create space"() {
        when:
        spaceRepository.save(Space.of(TEST_SPACE))

        then:
        spaceRepository.exists(TEST_SPACE)
    }

    def "should tell if space exists"() {
        when:
        def spaceExists = spaceRepository.exists(TEST_SPACE)

        then:
        !spaceExists
    }

    def "should remove space"() {
        given:
        spaceRepository.save(Space.of(TEST_SPACE))

        when:
        spaceRepository.remove(TEST_SPACE)

        then:
        !spaceRepository.exists(TEST_SPACE)
    }

    def "should return space"() {
        given:
        spaceRepository.save(Space.of(TEST_SPACE))

        when:
        Space space = spaceRepository.get(TEST_SPACE)

        then:
        space.name == TEST_SPACE
    }

    def "should update space"() {
        given:
        spaceRepository.save(Space.of(TEST_SPACE))
        BucketService bucketService = bucketServiceFactory.buildBucketService(TEST_SPACE)
        bucketService.createBucket("sampleBucket")

        when:
        spaceRepository.update(Space.of(TEST_SPACE, ["sampleBucket"] as Set))

        then:
        Space updated = spaceRepository.get(TEST_SPACE)
        updated.buckets.size() == 1
        updated.buckets == ["sampleBucket"] as Set
    }

    def "should return error when space name is not unique"() {
        given:
        spaceRepository.save(Space.of(TEST_SPACE))

        when:
        spaceRepository.save(Space.of(TEST_SPACE))

        then:
        thrown SpaceNameNotUniqueException
    }
}
