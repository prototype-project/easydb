package com.easydb.easydb.domain.bucket

import com.easydb.easydb.BaseSpecification
import com.easydb.easydb.domain.space.Space
import spock.lang.Shared

import static com.easydb.easydb.ElementTestBuilder.builder


class BucketServiceSpec extends BaseSpecification {

    public static final String TEST_BUCKET_NAME = "peoples"
    public static final String TEST_SPACE = "testSpace"

    @Shared
    BucketService bucketService

    def setupSpec() {
        Space space = Space.of(TEST_SPACE)
        spaceRepository.save(space)
        this.bucketService = bucketServiceFactory.buildBucketService(TEST_SPACE)
    }

    def cleanupSpec() {
        spaceRepository.remove(TEST_SPACE)
    }

    def cleanup() {
        try {
            bucketService.removeBucket(TEST_BUCKET_NAME)
        } catch (Exception ignored) {}
    }

    def "should remove bucket"() {
        given:
        bucketService.addElement(builder().bucketName(TEST_BUCKET_NAME).build())

        when:
        bucketService.removeBucket(TEST_BUCKET_NAME)

        then:
        !bucketService.bucketExists(TEST_BUCKET_NAME)
    }

    def "should add element to bucket"() {
        given:
        Element toCreate = builder().bucketName(TEST_BUCKET_NAME).build()

        when:
        bucketService.addElement(toCreate)

        then:
        Element fromDb = bucketService.getElement(TEST_BUCKET_NAME, toCreate.getId())
        with(fromDb) {
            id == toCreate.id
            bucketName == toCreate.bucketName
        }
    }

    def "should remove element from bucket"() {
        given:
        Element toCreate = builder().bucketName(TEST_BUCKET_NAME).build()
        bucketService.addElement(toCreate)

        when:
        bucketService.removeElement(TEST_BUCKET_NAME, toCreate.id)

        then:
        !bucketService.elementExists(TEST_BUCKET_NAME, toCreate.id)
    }

    def "should update element in bucket"() {
        given:
        Element toCreate = builder().bucketName(TEST_BUCKET_NAME).build()
        bucketService.addElement(toCreate)

        and:
        Element toUpdate = builder().bucketName(TEST_BUCKET_NAME).fields(
                [ElementField.of('lastName', 'Snow')]
        )
                .id(toCreate.id)
                .build()

        when:
        bucketService.updateElement(toUpdate)

        then:
        bucketService.getElement(TEST_BUCKET_NAME, toCreate.id).getFieldValue("lastName") == "Snow"
        bucketService.getElement(TEST_BUCKET_NAME, toCreate.id).id == toCreate.id
    }

    def "should throw exception when trying to update element in nonexistent bucket"() {
        given:
        Element toUpdate = builder().bucketName(TEST_BUCKET_NAME).fields(
                [ElementField.of('lastName', 'Snow')]
        ).build()

        when:
        bucketService.updateElement(toUpdate)

        then:
        thrown BucketDoesNotExistException
    }

    def "should throw exception when trying to update nonexistent element"() {
        given:
        Element toCreate = builder().bucketName(TEST_BUCKET_NAME).build()

        bucketService.addElement(toCreate)

        and:
        Element toUpdate = builder().bucketName(TEST_BUCKET_NAME)
                .fields([ElementField.of('firstName', 'Snow')])
                .id("nonexistentId")
                .build()

        when:
        bucketService.updateElement(toUpdate)

        then:
        thrown ElementDoesNotExistException
    }

    def "should get element from bucket"() {
        given:
        Element toCreate = builder().bucketName(TEST_BUCKET_NAME).build()

        bucketService.addElement(toCreate)

        when:
        Element elementFromBucket = bucketService.getElement(TEST_BUCKET_NAME, toCreate.id)

        then:
        with(elementFromBucket) {
            id == toCreate.id
            bucketName == toCreate.bucketName
        }
    }

    def "should throw exception when trying to get element from nonexistent bucket"() {
        when:
        bucketService.getElement("nonexistentBucket", "someId")

        then:
        thrown BucketDoesNotExistException
    }

    def "should throw exception when trying to get nonexistent element"() {
        given:
        Element toCreate = builder().bucketName(TEST_BUCKET_NAME).build()

        bucketService.addElement(toCreate)

        when:
        bucketService.getElement(TEST_BUCKET_NAME, "nonexistentId")

        then:
        thrown ElementDoesNotExistException
    }

    def "should get all elements from bucket"() {
        given:
        Element toCreate = builder().bucketName(TEST_BUCKET_NAME).build()

        bucketService.addElement(toCreate)

        when:
        List<Element> elementsFromBucket = bucketService.filterElements(getDefaultBucketQuery())

        then:
        elementsFromBucket.size() == 1
        with(elementsFromBucket[0]) {
            id == toCreate.id
            bucketName == toCreate.bucketName
        }
    }

    def "should return empty list when trying to get elements from nonexistent bucket"() {
        when:
        List<Element> elementsFromBucket = bucketService.filterElements(getDefaultBucketQuery())

        then:
        elementsFromBucket.size() == 0
    }

    def "should return empty list when getting all elements from empty bucket"() {
        given:
        Element toCreate = builder().bucketName(TEST_BUCKET_NAME).build()

        bucketService.addElement(toCreate)

        and:
        bucketService.removeElement(TEST_BUCKET_NAME, toCreate.getId())

        when:
        List<Element> elementsFromBucket = bucketService.filterElements(getDefaultBucketQuery())

        then:
        elementsFromBucket.size() == 0
    }

    def "should return number of elements in bucket"() {
        given:
        bucketService.addElement(builder().bucketName(TEST_BUCKET_NAME).build())
        bucketService.addElement(builder().bucketName(TEST_BUCKET_NAME).build())

        expect:
        bucketService.getNumberOfElements(TEST_BUCKET_NAME) == 2

    }

    static BucketQuery getDefaultBucketQuery() {
        return BucketQuery.of(TEST_BUCKET_NAME, 20, 0);
    }
}