package com.easydb.easydb.domain

import com.easydb.easydb.domain.bucket.BucketOrElementDoesNotExistException
import com.easydb.easydb.domain.bucket.dto.ElementCreateDto
import com.easydb.easydb.domain.bucket.dto.ElementFieldDto
import com.easydb.easydb.domain.bucket.dto.ElementQueryDto
import com.easydb.easydb.domain.bucket.dto.ElementUpdateDto
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDefinitionCreateDto
import com.easydb.easydb.domain.space.SpaceDefinitionQueryDto
import spock.lang.Shared
import spock.lang.Specification


class SpaceSpec extends Specification {

    public static final String TEST_BUCKET_NAME = "people"

    @Shared
    Space space

    def setupSpec() {
        SpaceDefinitionQueryDto spaceDefinition = SpaceTestConfig.SPACE_DEFINITION_REPOSITORY
                .save(SpaceDefinitionCreateDto.of("testSpace"))
        space = SpaceTestConfig.SPACE_FACTORY.buildSpace(spaceDefinition)
    }

    def cleanup() {
        space.removeBucket(TEST_BUCKET_NAME)
    }

    def "should remove bucket"() {
        given:
        space.addElement(ElementCreateDto.of(TEST_BUCKET_NAME, [
                ElementFieldDto.of("firstName", "John")
        ]))

        when:
        space.removeBucket(TEST_BUCKET_NAME)

        then:
        !space.bucketExists(TEST_BUCKET_NAME)
    }

    def "should add element to bucket"() {
        given:
        ElementCreateDto elementToCreate = ElementCreateDto.of(TEST_BUCKET_NAME, [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')
        ])

        when:
        ElementQueryDto createdElement = space.addElement(elementToCreate)

        then:
        ElementQueryDto fromDb = space.getElement(TEST_BUCKET_NAME, createdElement.getId())
        with(fromDb) {
            id == createdElement.id
            bucketName == createdElement.bucketName
        }
    }

    def "should remove element from bucket"() {
        given:
        ElementCreateDto elementToCreate = ElementCreateDto.of(TEST_BUCKET_NAME, [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')
        ])
        ElementQueryDto createdElement = space.addElement(elementToCreate)

        when:
        space.removeElement(TEST_BUCKET_NAME, createdElement.id)

        then:
        !space.elementExists(TEST_BUCKET_NAME, createdElement.id)
    }

    def "should update element in bucket"() {
        given:
        ElementCreateDto elementToCreate = ElementCreateDto.of(TEST_BUCKET_NAME, [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')])

        ElementQueryDto createdElement = space.addElement(elementToCreate)

        and:
        ElementUpdateDto elementToUpdate = ElementUpdateDto.of(TEST_BUCKET_NAME, createdElement.id,
                [ElementFieldDto.of('lastName', 'Snow')])

        when:
        space.updateElement(elementToUpdate)

        then:
        space.getElement(TEST_BUCKET_NAME, createdElement.id).getFieldValue("lastName") == "Snow"
        space.getElement(TEST_BUCKET_NAME, createdElement.id).id == createdElement.id
    }

    def "should throw exception when trying to update element in nonexistent bucket"() {
        given:
        ElementUpdateDto elementToUpdate = ElementUpdateDto.of("nonexistentBucket", "someId",
                [ElementFieldDto.of('lastName', 'Snow')])

        when:
        space.updateElement(elementToUpdate)

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should throw exception when trying to update nonexistent element"() {
        given:
        space.addElement(ElementCreateDto.of(TEST_BUCKET_NAME, [
                ElementFieldDto.of("firstName", "John")
        ]))

        and:
        ElementUpdateDto elementToUpdate = ElementUpdateDto.of(TEST_BUCKET_NAME, 'nonexistentId',
                [ElementFieldDto.of('firstName', 'Snow')])

        when:
        space.updateElement(elementToUpdate)

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should get element from bucket"() {
        given:
        ElementCreateDto elementToCreate = ElementCreateDto.of(TEST_BUCKET_NAME, [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')
        ])

        ElementQueryDto createdElement = space.addElement(elementToCreate)

        when:
        ElementQueryDto elementFromBucket = space.getElement(TEST_BUCKET_NAME, createdElement.id)

        then:
        with(elementFromBucket) {
            id == createdElement.id
            bucketName == createdElement.bucketName
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
        space.addElement(ElementCreateDto.of(TEST_BUCKET_NAME, [
                ElementFieldDto.of("firstName", "John")
        ]))

        when:
        space.getElement(TEST_BUCKET_NAME, "nonexistentId")

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should get all elements from bucket"() {
        given:
        ElementCreateDto elementToCreate = ElementCreateDto.of(TEST_BUCKET_NAME, [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')
        ])

        ElementQueryDto createdElement = space.addElement(elementToCreate)

        when:
        List<ElementQueryDto> elementsFromBucket = space.getAllElements(TEST_BUCKET_NAME)

        then:
        elementsFromBucket.size() == 1
        with(elementsFromBucket[0]) {
            id == createdElement.id
            bucketName == createdElement.bucketName
        }
    }

    def "should return empty list when trying to get elements from nonexistent bucket"() {
        when:
        List<ElementQueryDto> elementsFromBucket = space.getAllElements(TEST_BUCKET_NAME)

        then:
        elementsFromBucket.size() == 0
    }

    def "should return empty list when getting all elements from empty bucket"() {
        given:
        ElementQueryDto addedElement = space.addElement(ElementCreateDto.of(TEST_BUCKET_NAME, [
                ElementFieldDto.of("firstName", "John")
        ]))

        and:
        space.removeElement(TEST_BUCKET_NAME, addedElement.getId())

        when:
        List<ElementQueryDto> elementsFromBucket = space.getAllElements(TEST_BUCKET_NAME)

        then:
        elementsFromBucket.size() == 0
    }
}