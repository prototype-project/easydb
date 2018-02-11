package com.easydb.easydb.domain.space

import spock.lang.Specification

class SpaceFactorySpec extends Specification {
    SpaceFactory spaceFactory = SpaceTestConfig.SPACE_FACTORY

    def "should build space"() {
        when:
        Space space = spaceFactory.buildSpace(SpaceDefinition.of("testSpace"))

        then:
        space != null
    }
}