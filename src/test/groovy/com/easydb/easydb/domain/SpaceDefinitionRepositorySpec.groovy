package com.easydb.easydb.domain

import com.easydb.easydb.domain.space.SpaceDefinitionCreateDto
import com.easydb.easydb.domain.space.SpaceDefinitionQueryDto
import com.easydb.easydb.domain.space.SpaceDefinitionRepository
import com.easydb.easydb.domain.space.SpaceNameNotUniqueException
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

class SpaceDefinitionRepositorySpec extends Specification {

    SpaceDefinitionRepository spaceRepository = SpaceTestConfig.createSpaceRepository()

    def "should create space"() {
        when:
        spaceRepository.save(SpaceDefinitionCreateDto.of("testSpace"))

        then:
        spaceRepository.exists("testSpace")
    }

    def "should tell if space exists"() {
        when:
        def spaceExists = spaceRepository.exists("testSpace")

        then:
        !spaceExists
    }

    def "should return space definition"() {
        given:
        spaceRepository.save(SpaceDefinitionCreateDto.of("testSpace"))

        when:
        SpaceDefinitionQueryDto spaceDefinitionQueryDto = spaceRepository.get("testSpace")

        then:
        spaceDefinitionQueryDto.spaceName == "testSpace"
    }

    def "should return error when space name is not unique"() {
        given:
        spaceRepository.save(SpaceDefinitionCreateDto.of("testSpace"))

        when:
        spaceRepository.save(SpaceDefinitionCreateDto.of("testSpace"))

        then:
        thrown SpaceNameNotUniqueException
    }
}
