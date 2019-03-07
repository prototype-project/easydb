package com.easydb.easydb.infrastructure.bucket

import com.easydb.easydb.ElementUtils
import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.domain.bucket.*
import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory
import com.easydb.easydb.domain.bucket.transactions.BucketRepository
import com.easydb.easydb.domain.space.SpaceRepository
import org.springframework.beans.factory.annotation.Autowired

import static com.easydb.easydb.ElementTestBuilder.builder

class BucketServiceSpec extends IntegrationWithCleanedDatabaseSpec implements ElementUtils {

    BucketService bucketService

    @Autowired BucketServiceFactory bucketServiceFactory

    @Autowired SpaceRepository spaceRepository

    @Autowired BucketRepository bucketRepository

    def setup() {
        bucketService = bucketServiceFactory.buildBucketService(TEST_SPACE)
        bucketService.createBucket(TEST_BUCKET_NAME)
    }

    def cleanup() {
        try {
            bucketService.removeBucket(TEST_BUCKET_NAME)
        } catch (Exception ignored) {}
    }

    def "should remove bucket"() {
        when:
        bucketService.removeBucket(TEST_BUCKET_NAME)

        then:
        !bucketService.bucketExists(TEST_BUCKET_NAME)
    }

    def "should throw error when trying to remove not existing bucket"() {
        when:
        bucketService.removeBucket("notExistingBucket")

        then:
        thrown BucketDoesNotExistException
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

    def "should throw error when trying add already existing element"() {
        given:
        Element toCreate = builder().bucketName(TEST_BUCKET_NAME).build()

        bucketService.addElement(toCreate)

        when:
        bucketService.addElement(toCreate)

        then:
        thrown ElementAlreadyExistsException
    }

    def "should update space's buckets when creating bucket"() {
        expect:
        spaceRepository.get(TEST_SPACE).buckets == [TEST_BUCKET_NAME] as Set
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

    def "should throw error when trying to remove element from not existing bucket"() {
        when:
        bucketService.removeElement("notExistingBucket", "notExistingElement")

        then:
        thrown BucketDoesNotExistException
    }

    def "should throw error when trying to remove not existing element"() {
        given:
        Element toCreate = builder().bucketName(TEST_BUCKET_NAME).build()

        bucketService.addElement(toCreate)

        when:
        bucketService.removeElement(TEST_BUCKET_NAME, "notExistingElement")

        then:
        thrown ElementDoesNotExistException
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
        getFieldValue(bucketService.getElement(TEST_BUCKET_NAME, toCreate.id), 'lastName') == "Snow"
        bucketService.getElement(TEST_BUCKET_NAME, toCreate.id).id == toCreate.id
    }

    def "should throw exception when trying to update element in nonexistent bucket"() {
        given:
        Element toUpdate = builder().bucketName("nonExistentBucket").fields(
                [ElementField.of('lastName', 'Snow')]
        ).build()

        when:
        bucketService.updateElement(toUpdate)

        then:
        thrown BucketDoesNotExistException
    }

    def "should throw exception when trying to update nonexistent element"() {
        given:
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
            fields.toSet() == toCreate.fields.toSet()
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
            fields == toCreate.fields
        }
    }

    def "should throw exception when trying to get elements from nonexistent bucket"() {
        when:
        bucketService.filterElements(BucketQuery.of("nonExistentBucket", 20, 0))

        then:
        thrown(BucketDoesNotExistException)
    }

    def "should return empty list when getting all elements from empty bucket"() {
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

    def "should throw error when trying to get number of elements from not existing bucket"() {
        when:
        bucketService.getNumberOfElements("notExistingBucket")

        then:
        thrown BucketDoesNotExistException
    }

    static BucketQuery getDefaultBucketQuery() {
        return BucketQuery.of(TEST_BUCKET_NAME, 20, 0);
    }
}
