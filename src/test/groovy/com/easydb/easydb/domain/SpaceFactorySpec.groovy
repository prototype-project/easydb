package com.easydb.easydb.domain

import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDefinition
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
        SpaceDefinition spaceDefinition = SpaceDefinition.of("testSpace")

        spaceDefinition = spaceDefinitionRepository.save(spaceDefinition)

        when:
        Space space = spaceFactory.buildSpace(spaceDefinition)

        then:
        space != null
    }
}
