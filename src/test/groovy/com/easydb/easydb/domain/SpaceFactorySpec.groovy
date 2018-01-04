package com.easydb.easydb.domain

import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDefinitionCreateDto
import com.easydb.easydb.domain.space.SpaceDefinitionQueryDto
import com.easydb.easydb.domain.space.SpaceDefinitionRepository
import com.easydb.easydb.domain.space.SpaceFactory
import spock.lang.Specification

class SpaceFactorySpec extends Specification {
    SpaceFactory spaceFactory = SpaceTestConfig.SPACE_FACTORY
    SpaceDefinitionRepository spaceDefinitionRepository = SpaceTestConfig.SPACE_DEFINITION_REPOSITORY

    def cleanup() {
        spaceDefinitionRepository.remove("testSpace")
    }

    def "should build space"() {
        given:
        SpaceDefinitionQueryDto spaceDefinition = spaceDefinitionRepository.save(SpaceDefinitionCreateDto.of("testSpace"))

        when:
        Space space = spaceFactory.buildSpace(spaceDefinition)

        then:
        space != null
    }
}
