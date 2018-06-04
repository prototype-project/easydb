package com.easydb.easydb.domain.space

import com.easydb.easydb.domain.bucket.BucketOrElementDoesNotExistException
import com.easydb.easydb.domain.bucket.BucketQuery
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField
import spock.lang.Shared

import static com.easydb.easydb.ElementTestBuilder.builder


class SpaceServiceSpec extends BaseSpecification {

    public static final String TEST_BUCKET_NAME = "people"
    public static final String TEST_SPACE = "testSpace"

    @Shared
    SpaceService spaceService

    def setupSpec() {
        Space space = Space.of(TEST_SPACE)
        spaceRepository.save(space)
        this.spaceService = spaceFactory.buildSpaceService(space)
    }

    def cleanupSpec() {
        spaceRepository.remove(TEST_SPACE)
    }

    def cleanup() {
        spaceService.removeBucket(TEST_BUCKET_NAME)
    }

    def "should remove bucket"() {
        given:
        spaceService.addElement(builder().build())

        when:
        spaceService.removeBucket(TEST_BUCKET_NAME)

        then:
        !spaceService.bucketExists(TEST_BUCKET_NAME)
    }

    def "should add element to bucket"() {
        given:
        Element toCreate = builder().build()

        when:
        spaceService.addElement(toCreate)

        then:
        Element fromDb = spaceService.getElement(TEST_BUCKET_NAME, toCreate.getId())
        with(fromDb) {
            id == toCreate.id
            bucketName == toCreate.bucketName
        }
    }

    def "should remove element from bucket"() {
        given:
        Element toCreate = builder().build()
        when:
        spaceService.removeElement(TEST_BUCKET_NAME, toCreate.id)

        then:
        !spaceService.elementExists(TEST_BUCKET_NAME, toCreate.id)
    }

    def "should update element in bucket"() {
        given:
        Element toCreate = builder().build()
        spaceService.addElement(toCreate)

        and:
        Element toUpdate = builder().fields(
                [ElementField.of('lastName', 'Snow')]
        )
        .id(toCreate.id)
        .build()

        when:
        spaceService.updateElement(toUpdate)

        then:
        spaceService.getElement(TEST_BUCKET_NAME, toCreate.id).getFieldValue("lastName") == "Snow"
        spaceService.getElement(TEST_BUCKET_NAME, toCreate.id).id == toCreate.id
    }

    def "should throw exception when trying to update element in nonexistent bucket"() {
        given:
        Element toUpdate = builder().fields(
                [ElementField.of('lastName', 'Snow')]
        ).build()

        when:
        spaceService.updateElement(toUpdate)

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should throw exception when trying to update nonexistent element"() {
        given:
        Element toCreate = builder().build()

        spaceService.addElement(toCreate)

        and:
        Element toUpdate = builder()
                .fields([ElementField.of('firstName', 'Snow')])
                .id("nonexistentId")
                .build()

        when:
        spaceService.updateElement(toUpdate)

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should get element from bucket"() {
        given:
        Element toCreate = builder().build()

        spaceService.addElement(toCreate)

        when:
        Element elementFromBucket = spaceService.getElement(TEST_BUCKET_NAME, toCreate.id)

        then:
        with(elementFromBucket) {
            id == toCreate.id
            bucketName == toCreate.bucketName
        }
    }

    def "should throw exception when trying to get element from nonexistent bucket"() {
        when:
        spaceService.getElement("nonexistentBucket", "someId")

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should throw exception when trying to get nonexistent element"() {
        given:
        Element toCreate = builder().build()

        spaceService.addElement(toCreate)

        when:
        spaceService.getElement(TEST_BUCKET_NAME, "nonexistentId")

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should get all elements from bucket"() {
        given:
        Element toCreate = builder().build()

        spaceService.addElement(toCreate)

        when:
        List<Element> elementsFromBucket = spaceService.filterElements(getDefaultBucketQuery())

        then:
        elementsFromBucket.size() == 1
        with(elementsFromBucket[0]) {
            id == toCreate.id
            bucketName == toCreate.bucketName
        }
    }

    def "should return empty list when trying to get elements from nonexistent bucket"() {
        when:
        List<Element> elementsFromBucket = spaceService.filterElements(getDefaultBucketQuery())

        then:
        elementsFromBucket.size() == 0
    }

    def "should return empty list when getting all elements from empty bucket"() {
        given:
        Element toCreate = builder().build()

        spaceService.addElement(toCreate)

        and:
        spaceService.removeElement(TEST_BUCKET_NAME, toCreate.getId())

        when:
        List<Element> elementsFromBucket = spaceService.filterElements(getDefaultBucketQuery())

        then:
        elementsFromBucket.size() == 0
    }

    def "should return number of elements in bucket"() {
        given:
        spaceService.addElement(builder().build())
        spaceService.addElement(builder().build())

        expect:
        spaceService.getNumberOfElements(TEST_BUCKET_NAME) == 2

    }

    static BucketQuery getDefaultBucketQuery() {
        return BucketQuery.of(TEST_BUCKET_NAME, 20, 0);
    }
}