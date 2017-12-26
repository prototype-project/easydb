package com.easydb.easydb.domain

import spock.lang.Specification


class SpaceSpec extends Specification {

    def space = SpaceConfig.createSpace()

    def "should create bucket"() {
        when:
        space.createBucket("people")

        then:
        space.bucketExists("people")
    }

    def "should remove bucket"() {
        given:
        space.createBucket("people")

        when:
        space.removeBucket("people")

        then:
        !space.bucketExists("people")
    }

    def "should throw exception when trying to create bucket with non unique name"() {
        given:
        space.createBucket("people")

        when:
        space.createBucket("people")

        then:
        thrown BucketExistsException
    }

    def "should throw exception when trying to remove nonexistent bucket"() {
        when:
        space.removeBucket("people")

        then:
        thrown BucketDoesNotExistException
    }

    def "should add element to bucket"() {
        given:
        space.createBucket("people")

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
            name == createdElement.name
        }
    }

    def "should remove element from bucket"() {
        given:
        space.createBucket("people")

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

    def "should throw exception when removing nonexistent element"() {
        when:
        space.removeElement("people", "notExistingId")

        then:
        thrown ElementDoesNotExistException
    }

    def "should update element in bucket"() {
        given:
        space.createBucket("people")

        and:
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
        space.createBucket("people")

        and:
        ElementCreateDto elementToCreate = ElementCreateDto.of("people", [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')])

        ElementQueryDto createdElement = space.addElement(elementToCreate)

        and:
        ElementUpdateDto elementToUpdate = ElementUpdateDto.of("nonexistentBucket", createdElement.id,
                [ElementFieldDto.of('lastName', 'Snow')])

        when:
        space.updateElement(elementToUpdate)

        then:
        thrown BucketDoesNotExistException
    }

    def "should throw exception when trying to update nonexistent element"() {
        given:
        space.createBucket("people")

        and:
        ElementCreateDto elementToCreate = ElementCreateDto.of("people", [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')])

        space.addElement(elementToCreate)

        and:
        ElementUpdateDto elementToUpdate = ElementUpdateDto.of("people", 'nonexistentId',
                [ElementFieldDto.of('lastName', 'Snow')])

        when:
        space.updateElement(elementToUpdate)

        then:
        thrown BucketElementDoesNotExistException
    }

    def "should get element from bucket"() {
        given:
        space.createBucket("people")

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
            name == createdElement.name
        }
    }

    def "should throw exception when trying to get element from nonexistent bucket"() {
        given:
        space.createBucket("people")

        and:
        ElementCreateDto elementToCreate = ElementCreateDto.of("people", [
                ElementFieldDto.of('firstName', 'John'),
                ElementFieldDto.of('lastName', 'Smith'),
                ElementFieldDto.of('email', 'john.smith@op.pl')
        ])

        ElementQueryDto createdElement = space.addElement(elementToCreate)

        when:
        space.getElement("nonexistentBucket", createdElement.id)

        then:
        thrown BucketDoesNotExistException
    }

    def "should throw exception when trying to get nonexistent element"() {

    }

    def "should get all elements from bucket"() {
        given:
        space.createBucket("people")

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
            name == createdElement.name
        }
    }

    def "should throw exception when trying to get elements from nonexistent bucket"() {

    }

    def "should return empty list when getting all elements from empty bucket"() {

    }
}