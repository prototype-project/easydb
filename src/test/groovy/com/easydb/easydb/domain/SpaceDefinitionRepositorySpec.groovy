package com.easydb.easydb.domain

import com.easydb.easydb.domain.space.SpaceDefinitionCreateDto
import com.easydb.easydb.domain.space.SpaceDefinitionQueryDto
import com.easydb.easydb.domain.space.SpaceDefinitionRepository
import com.easydb.easydb.domain.space.SpaceNameNotUniqueException
import spock.lang.Specification

class SpaceDefinitionRepositorySpec extends Specification {

    public static final String TEST_SPACE = "testSpace"
    SpaceDefinitionRepository spaceRepository = SpaceTestConfig.SPACE_DEFINITION_REPOSITORY

    def cleanup() {
        spaceRepository.remove(TEST_SPACE)
    }

    def "should create space"() {
        when:
        spaceRepository.save(SpaceDefinitionCreateDto.of(TEST_SPACE))

        then:
        spaceRepository.exists(TEST_SPACE)
    }

    def "should tell if space exists"() {
        when:
        def spaceExists = spaceRepository.exists(TEST_SPACE)

        then:
        !spaceExists
    }

    def "should remove space definition"() {
        given:
        spaceRepository.save(SpaceDefinitionCreateDto.of(TEST_SPACE))

        when:
        spaceRepository.remove(TEST_SPACE)

        then:
        !spaceRepository.exists(TEST_SPACE)
    }

    def "should return space definition"() {
        given:
        spaceRepository.save(SpaceDefinitionCreateDto.of(TEST_SPACE))

        when:
        SpaceDefinitionQueryDto spaceDefinitionQueryDto = spaceRepository.get(TEST_SPACE)

        then:
        spaceDefinitionQueryDto.spaceName == TEST_SPACE
    }

    def "should return error when space name is not unique"() {
        given:
        spaceRepository.save(SpaceDefinitionCreateDto.of(TEST_SPACE))

        when:
        spaceRepository.save(SpaceDefinitionCreateDto.of(TEST_SPACE))

        then:
        thrown SpaceNameNotUniqueException
    }
}
