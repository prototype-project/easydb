package com.easydb.easydb.domain.space

import com.easydb.easydb.BaseSpecification


class SpaceRepositorySpec extends BaseSpecification {

    public static final String TEST_SPACE = "testSpace"

    def cleanup() {
        try {
            spaceRepository.remove(TEST_SPACE)
        } catch (SpaceDoesNotExistException ignored) {}
    }

    def "should create space"() {
        when:
        spaceRepository.save(Space.of(TEST_SPACE))

        then:
        spaceRepository.exists(TEST_SPACE)
    }

    def "should tell if space exists"() {
        when:
        def spaceExists = spaceRepository.exists(TEST_SPACE)

        then:
        !spaceExists
    }

    def "should remove space"() {
        given:
        spaceRepository.save(Space.of(TEST_SPACE))

        when:
        spaceRepository.remove(TEST_SPACE)

        then:
        !spaceRepository.exists(TEST_SPACE)
    }

    def "should return space"() {
        given:
        spaceRepository.save(Space.of(TEST_SPACE))

        when:
        Space spaceDefinition = spaceRepository.get(TEST_SPACE)

        then:
        spaceDefinition.name == TEST_SPACE
    }

    def "should return error when space name is not unique"() {
        given:
        spaceRepository.save(Space.of(TEST_SPACE))

        when:
        spaceRepository.save(Space.of(TEST_SPACE))

        then:
        thrown SpaceNameNotUniqueException
    }
}
