package com.easydb.easydb.domain

import com.easydb.easydb.domain.bucket.BucketOrElementDoesNotExistException
import com.easydb.easydb.domain.bucket.dto.ElementCreateDto
import com.easydb.easydb.domain.bucket.dto.ElementFieldDto
import com.easydb.easydb.domain.bucket.dto.ElementQueryDto
import com.easydb.easydb.domain.bucket.dto.ElementUpdateDto
import spock.lang.Specification


class SpaceSpec extends Specification {

    def space = SpaceTestConfig.createSpace()

    def "should remove bucket"() {
        given:
        space.addElement(ElementCreateDto.of("people", [
                ElementFieldDto.of("firstName", "John")
        ]))

        when:
        space.removeBucket("people")

        then:
        !space.bucketExists("people")
    }

    def "should add element to bucket"() {
        given:
        ElementCreateDto elementToCreate = ElementCreateDto.of("people", [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')
        ])

        when:
        ElementQueryDto createdElement = space.addElement(elementToCreate)

        then:
        ElementQueryDto fromDb = space.getElement("people", createdElement.getId())
        with(fromDb) {
            id == createdElement.id
            bucketName == createdElement.bucketName
        }
    }

    def "should remove element from bucket"() {
        given:
        ElementCreateDto elementToCreate = ElementCreateDto.of("people", [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')
        ])
        ElementQueryDto createdElement = space.addElement(elementToCreate)

        when:
        space.removeElement("people", createdElement.id)

        then:
        !space.elementExists("people", createdElement.id)
    }

    def "should update element in bucket"() {
        given:
        ElementCreateDto elementToCreate = ElementCreateDto.of("people", [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')])

        ElementQueryDto createdElement = space.addElement(elementToCreate)

        and:
        ElementUpdateDto elementToUpdate = ElementUpdateDto.of("people", createdElement.id,
                [ElementFieldDto.of('lastName', 'Snow')])

        when:
        space.updateElement(elementToUpdate)

        then:
        space.getElement("people", createdElement.id).getFieldValue("lastName") == "Snow"
        space.getElement("people", createdElement.id).id == createdElement.id
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
        space.addElement(ElementCreateDto.of("people", [
                ElementFieldDto.of("firstName", "John")
        ]))

        and:
        ElementUpdateDto elementToUpdate = ElementUpdateDto.of("people", 'nonexistentId',
                [ElementFieldDto.of('firstName', 'Snow')])

        when:
        space.updateElement(elementToUpdate)

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should get element from bucket"() {
        given:
        ElementCreateDto elementToCreate = ElementCreateDto.of("people", [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')
        ])

        ElementQueryDto createdElement = space.addElement(elementToCreate)

        when:
        ElementQueryDto elementFromBucket = space.getElement("people", createdElement.id)

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
        space.addElement(ElementCreateDto.of("people", [
                ElementFieldDto.of("firstName", "John")
        ]))

        when:
        space.getElement("people", "nonexistentId")

        then:
        thrown BucketOrElementDoesNotExistException
    }

    def "should get all elements from bucket"() {
        given:
        ElementCreateDto elementToCreate = ElementCreateDto.of("people", [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')
        ])

        ElementQueryDto createdElement = space.addElement(elementToCreate)

        when:
        List<ElementQueryDto> elementsFromBucket = space.getAllElements("people")

        then:
        elementsFromBucket.size() == 1
        with(elementsFromBucket[0]) {
            id == createdElement.id
            bucketName == createdElement.bucketName
        }
    }

    def "should return empty list when trying to get elements from nonexistent bucket"() {
        when:
        List<ElementQueryDto> elementsFromBucket = space.getAllElements("people")

        then:
        elementsFromBucket.size() == 0
    }

    def "should return empty list when getting all elements from empty bucket"() {
        given:
        ElementQueryDto addedElement = space.addElement(ElementCreateDto.of("people", [
                ElementFieldDto.of("firstName", "John")
        ]))

        and:
        space.removeElement("people", addedElement.getId())

        when:
        List<ElementQueryDto> elementsFromBucket = space.getAllElements("people")

        then:
        elementsFromBucket.size() == 0
    }
}