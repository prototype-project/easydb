package com.easydb.easydb

import com.easydb.easydb.domain.BucketDoesNotExistException
import com.easydb.easydb.domain.BucketExistsException
import com.easydb.easydb.domain.ElementCreateDto
import com.easydb.easydb.domain.ElementCreateFieldDto
import com.easydb.easydb.domain.ElementDoesNotExistException
import com.easydb.easydb.domain.ElementQueryDto
import com.easydb.easydb.domain.ElementUpdateDto
import com.easydb.easydb.domain.ElementUpdateFieldDto
import spock.lang.Specification

class SpaceSpec extends Specification {

    def space = SpaceConfig.createSpace();

    def "should create bucket"() {
        when:
        space.createBucket("people", ["firstName", "lastName", "email"])

        then:
        space.bucketExists("people")
    }

    def "should remove bucket"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        when:
        space.removeBucket("people")

        then:
        !space.bucketExists("people")
    }

    def "should throw error when trying to create bucket with non unique name"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        when:
        space.createBucket("people", ["firstName", "lastName", "email"])

        then:
        thrown BucketExistsException
    }

    def "should throw error when trying to remove nonexistent bucket"() {
        when:
        space.removeBucket("people")

        then:
        thrown BucketDoesNotExistException
    }

    def "should add element to bucket"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        ElementCreateDto elementToCreate = ElementCreateDto.of("bucketName",
                ElementCreateFieldDto.create('firstName', 'John'),
                ElementCreateFieldDto.create('lastName', 'Smith'),
                ElementCreateFieldDto.create('email', 'john.smith@op.pl'))

        when:
        ElementQueryDto createdElement = space.addElement(elementToCreate)

        then:
        createdElement == space.getElement("people", createdElement.getId())
    }

    def "should remove element from bucket"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        ElementCreateDto elementToCreate = ElementCreateDto.of("bucketName",
                ElementCreateFieldDto.create('firstName', 'John'),
                ElementCreateFieldDto.create('lastName', 'Smith'),
                ElementCreateFieldDto.create('email', 'john.smith@op.pl')
        )
        ElementQueryDto createdElement = space.addElement(exampleElement)

        when:
        space.removeElement("people", createdElement.id)

        then:
        !space.elementExists("people", createdElement.id)
    }

    def "should update element in bucket"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        ElementCreateDto elementToCreate = ElementCreateDto.of("bucketName",
                ElementCreateFieldDto.create('firstName', 'John'),
                ElementCreateFieldDto.create('lastName', 'Smith'),
                ElementCreateFieldDto.create('email', 'john.smith@op.pl'))

        ElementQueryDto createdElement = space.addElement(elementToCreate)

        ElementUpdateDto elementToUpdate = ElementUpdateDto.of("people", createdElement.id,
                ElementUpdateFieldDto.create('lastName', 'Snow')
        )

        when:
        space.updateElement(elementToUpdate)

        then:
        space.getElement("people", createdElement.id).getFieldValue("lastName") == "Snow"
    }

    def "should get element from bucket"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        ElementCreateDto elementToCreate = ElementCreateDto.of("bucketName",
                ElementCreateFieldDto.create('firstName', 'John'),
                ElementCreateFieldDto.create('lastName', 'Smith'),
                ElementCreateFieldDto.create('email', 'john.smith@op.pl'))

        ElementQueryDto createdElement = space.addElement(exampleElement)

        when:
        ElementQueryDto elementFromBucket = space.getElement("people", createdElement.id)

        then:
        createdElement == elementFromBucket
    }

    def "should get all elements from bucket"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        ElementCreateDto elementToCreate = ElementCreateDto.of("bucketName",
                ElementCreateFieldDto.create('firstName', 'John'),
                ElementCreateFieldDto.create('lastName', 'Smith'),
                ElementCreateFieldDto.create('email', 'john.smith@op.pl'))

        ElementQueryDto createdElement = space.addElement(exampleElement)

        when:
        List<ElementQueryDto> elementsFromBucket = space.getAllElements("people")

        then:
        elementsFromBucket.size() == 1
        elementsFromBucket[0] == createdElement
    }

    def "should throw error when removing nonexistent element"() {
        when:
        space.removeElement("people", "notExistingId")

        then:
        thrown ElementDoesNotExistException
    }
}
