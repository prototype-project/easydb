package com.easydb.easydb

import spock.lang.Shared

class SpaceSpec {

    @Shared
    def space = Space.of("testSpace")

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
        throw BucketDoesNotExistException
    }

    def "should add element to bucket"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        ElementCreateDto elementToCreate = ElementCreateDto.of("name", [
                ElementCreateFieldDto.create('firstName', 'John'),
                ElementCreateFieldDto.create('lastName', 'Smith'),
                ElementCreateFieldDto.create('email', 'john.smith@op.pl')
        ])

        when:
        ElementQueryDto createdElement = space.addElement(elementToCreate)

        then:
        createdElement == space.getElement(createdElement.id)
    }

    def "should remove element from bucket"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        ElementCreateDto elementToCreate = ElementCreateDto.of("name", [
                ElementFieldDto.create('firstName', 'John'),
                ElementFieldDto.create('lastName', 'Smith'),
                ElementFieldDto.create('email', 'john.smith@op.pl')
        ])
        ElementQueryDto createdElement = space.addElement(exampleElement)

        when:
        space.removeElement("people", createdElement.id)

        then:
        !space.elementExists("people", createdElement.id)
    }

    def "should update element in bucket"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        ElementCreateDto elementToCreate = ElementCreateDto.of("name", [
                ElementFieldDto.create('firstName', 'John'),
                ElementFieldDto.create('lastName', 'Smith'),
                ElementFieldDto.create('email', 'john.smith@op.pl')
        ])

        ElementQueryDto createdElement = space.addElement(elementToCreate)

        ElementUpdateDto elementToUpdate = ElementUpdateDto("people", createdElement.id, [
                ElementUpdateFieldDto.create('lastName', 'Snow')
        ])

        when:
        space.updateElement(elementToUpdate)

        then
        space.getElement(createdElement.id).getFieldValue("lastName") == "Snow"
    }

    def "should get element from bucket"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        ElementCreateDto exampleElement = ElementCreateDto.of("name", [
                ElementFieldDto.create('firstName', 'John'),
                ElementFieldDto.create('lastName', 'Smith'),
                ElementFieldDto.create('email', 'john.smith@op.pl')
        ])

        ElementQueryDto createdElement = space.addElement(exampleElement)

        when:
        ElementQueryDto elementFromBucket = space.getElement("people", createdElement.id)

        then:
        createdElement == elementFromBucket
    }

    def "should get all elements from bucket"() {
        given:
        space.createBucket("people", ["firstName", "lastName", "email"])

        ElementCreateDto exampleElement = ElementCreateDto.of("name", [
                ElementFieldDto.create('firstName', 'John'),
                ElementFieldDto.create('lastName', 'Smith'),
                ElementFieldDto.create('email', 'john.smith@op.pl')
        ])

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
        thrown ElementDoesNotExist()
    }
}
