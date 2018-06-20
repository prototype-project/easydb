package com.easydb.easydb.domain.space

import com.easydb.easydb.BaseSpecification
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.Element


class SpaceServiceSpec extends BaseSpecification {

    public static final String TEST_SPACE = "testSpace"

    def cleanup() {
        try {
            spaceService.remove(TEST_SPACE)
        } catch (SpaceDoesNotExistException ignored) {}
    }

    def "should create space"() {
        when:
        spaceService.save(Space.of(TEST_SPACE))

        then:
        spaceService.exists(TEST_SPACE)
    }

    def "should tell if space exists"() {
        when:
        def spaceExists = spaceService.exists(TEST_SPACE)

        then:
        !spaceExists
    }

    def "should remove space"() {
        given:
        spaceService.save(Space.of(TEST_SPACE))

        when:
        spaceService.remove(TEST_SPACE)

        then:
        !spaceService.exists(TEST_SPACE)
    }

    def "should return space"() {
        given:
        spaceService.save(Space.of(TEST_SPACE))

        when:
        Space space = spaceService.get(TEST_SPACE)

        then:
        space.name == TEST_SPACE
    }

    def "should update space"() {
        given:
        spaceService.save(Space.of(TEST_SPACE))
        BucketService bucketService = spaceService.bucketServiceForSpace(TEST_SPACE)
        Element sampleElement = ElementTestBuilder.builder().bucketName("sampleBucket").build()
        bucketService.addElement(sampleElement)

        when:
        spaceService.update(Space.of(TEST_SPACE, ["sampleBucket"] as Set))

        then:
        Space updated = spaceService.get(TEST_SPACE)
        updated.buckets.size() == 1
        updated.buckets == ["sampleBucket"] as Set

    }

    def "should return error when space name is not unique"() {
        given:
        spaceService.save(Space.of(TEST_SPACE))

        when:
        spaceService.save(Space.of(TEST_SPACE))

        then:
        thrown SpaceNameNotUniqueException
    }

    def "should remove all space's buckets when removing space"() {
        given:
        spaceService.save(Space.of(TEST_SPACE))
        BucketService bucketService = spaceService.bucketServiceForSpace(TEST_SPACE)
        Element sampleElement = ElementTestBuilder.builder().build()
        bucketService.addElement(sampleElement)

        when:
        spaceService.remove(TEST_SPACE)

        then:
        !spaceService.exists(TEST_SPACE)
        !bucketService.bucketExists(sampleElement.bucketName)
    }
}
