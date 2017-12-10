package com.easydb.easydb

import spock.lang.Specification

class BucketSpec extends Specification {
    def "should add element" () {
        given:
        Bucket bucket = exampleBucket()

        when:
        bucket.addNewElement(
                BucketElementField.create('firstName', 'John'),
                BucketElementField.create('lastName', 'Smith')
        )

        then:
        !bucket.isEmpty()
    }

    def "should remove element" () {
        given:
        Bucket bucket = exampleBucket()

        and:
        BucketElement addedElement = bucket.addNewElement(
                BucketElementField.create('firstName', 'John'),
                BucketElementField.create('lastName', 'Smith')
        )

        when:
        bucket.remove(addedElement.getId())

        then:
        bucket.isEmpty()
    }

    def "should throw error when trying to remove nonexistent element" () {

    }

    def "should return element with specified id"() {
        given:
        Bucket bucket = exampleBucket()

        and:
        BucketElement addedElement = bucket.addNewElement(
                BucketElementField.create('firstName', 'John'),
                BucketElementField.create('lastName', 'Smith')
        )

        when:
        BucketElement johnSmith = bucket.get(addedElement.getId())

        then:
        addedElement == johnSmith
    }

    def "should throw error when trying to get nonexistent element" () {

    }

    def "should return all elements" () {
        given:
        Bucket bucket = exampleBucket()

        and:
        BucketElement addedElement = bucket.addNewElement(
                BucketElementField.create('firstName', 'John'),
                BucketElementField.create('lastName', 'Smith')
        )

        when:
        List<BucketElement> allElements = bucket.all()

        then:
        allElements == [addedElement]
    }

    def "should return empty list when trying to get all elements from empty bucket" () {

    }

    BucketDefinition exampleDefinition() {
        BucketDefinition.builder()
                .withName('people')
                .withProperty('firstName')
                .withProperty('lastName')
                .build()
    }

    Bucket exampleBucket() {
        Bucket.of(exampleDefinition())
    }
}
