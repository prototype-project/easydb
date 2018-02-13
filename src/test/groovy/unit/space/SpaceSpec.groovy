package unit.space

import com.easydb.easydb.domain.bucket.BucketOrElementDoesNotExistException
import com.easydb.easydb.domain.bucket.BucketQuery
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDefinition
import spock.lang.Shared
import spock.lang.Specification

import static unit.ElementTestBuilder.builder


class SpaceSpec extends Specification {

    public static final String TEST_BUCKET_NAME = "people"
    public static final String TEST_SPACE = "testSpace"

    @Shared
    Space space

    def setupSpec() {
        SpaceDefinition spaceDefinition = SpaceDefinition.of(TEST_SPACE)
        SpaceTestConfig.SPACE_DEFINITION_REPOSITORY.save(spaceDefinition)
        space = SpaceTestConfig.SPACE_FACTORY.buildSpace(spaceDefinition)
    }

    def cleanupSpec() {
        SpaceTestConfig.SPACE_DEFINITION_REPOSITORY.remove(TEST_SPACE)
    }

    def cleanup() {
        space.removeBucket(TEST_BUCKET_NAME)
    }

    def "should remove bucket"() {
        given:
        space.addElement(builder().build())

        when:
        space.removeBucket(TEST_BUCKET_NAME)

        then:
        !space.bucketExists(TEST_BUCKET_NAME)
    }

    def "should add element to bucket"() {
        given:
        Element toCreate = builder().build()

        when:
        space.addElement(toCreate)

        then:
        Element fromDb = space.getElement(TEST_BUCKET_NAME, toCreate.getId())
        with(fromDb) {
            id == toCreate.id
            bucketName == toCreate.bucketName
        }
    }

    def "should remove element from bucket"() {
        given:
        Element toCreate = builder().build()
        when:
        space.removeElement(TEST_BUCKET_NAME, toCreate.id)

        then:
        !space.elementExists(TEST_BUCKET_NAME, toCreate.id)
    }

    def "should update element in bucket"() {
        given:
        Element toCreate = builder().build()
        space.addElement(toCreate)

        and:
        Element toUpdate = builder().fields(
                [ElementField.of('lastName', 'Snow')]
        )
        .id(toCreate.id)
        .build()

        when:
        space.updateElement(toUpdate)

        then:
        space.getElement(TEST_BUCKET_NAME, toCreate.id).getFieldValue("lastName") == "Snow"
        space.getElement(TEST_BUCKET_NAME, toCreate.id).id == toCreate.id
    }

    def "should throw exception when trying to update element in nonexistent bucket"() {
        given:
        Element toUpdate = builder().fields(
                [ElementField.of('lastName', 'Snow')]
        ).build()

        when:
        space.updateElement(toUpdate)

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should throw exception when trying to update nonexistent element"() {
        given:
        Element toCreate = builder().build()

        space.addElement(toCreate)

        and:
        Element toUpdate = builder()
                .fields([ElementField.of('firstName', 'Snow')])
                .id("nonexistentId")
                .build()

        when:
        space.updateElement(toUpdate)

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should get element from bucket"() {
        given:
        Element toCreate = builder().build()

        space.addElement(toCreate)

        when:
        Element elementFromBucket = space.getElement(TEST_BUCKET_NAME, toCreate.id)

        then:
        with(elementFromBucket) {
            id == toCreate.id
            bucketName == toCreate.bucketName
        }
    }

    def "should throw exception when trying to get element from nonexistent bucket"() {
        when:
        space.getElement("nonexistentBucket", "someId")

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should throw exception when trying to get nonexistent element"() {
        given:
        Element toCreate = builder().build()

        space.addElement(toCreate)

        when:
        space.getElement(TEST_BUCKET_NAME, "nonexistentId")

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should get all elements from bucket"() {
        given:
        Element toCreate = builder().build()

        space.addElement(toCreate)

        when:
        List<Element> elementsFromBucket = space.filterElements(getDefaultBucketQuery())

        then:
        elementsFromBucket.size() == 1
        with(elementsFromBucket[0]) {
            id == toCreate.id
            bucketName == toCreate.bucketName
        }
    }

    def "should return empty list when trying to get elements from nonexistent bucket"() {
        when:
        List<Element> elementsFromBucket = space.filterElements(getDefaultBucketQuery())

        then:
        elementsFromBucket.size() == 0
    }

    def "should return empty list when getting all elements from empty bucket"() {
        given:
        Element toCreate = builder().build()

        space.addElement(toCreate)

        and:
        space.removeElement(TEST_BUCKET_NAME, toCreate.getId())

        when:
        List<Element> elementsFromBucket = space.filterElements(getDefaultBucketQuery())

        then:
        elementsFromBucket.size() == 0
    }

    def "should return number of elements in bucket"() {
        given:
        space.addElement(builder().build())
        space.addElement(builder().build())

        expect:
        space.getNumberOfElements(TEST_BUCKET_NAME) == 2

    }

    static BucketQuery getDefaultBucketQuery() {
        return BucketQuery.of(TEST_BUCKET_NAME, 20, 0);
    }
}