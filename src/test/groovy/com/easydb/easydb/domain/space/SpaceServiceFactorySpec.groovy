package com.easydb.easydb.domain.space


class SpaceServiceFactorySpec extends BaseSpecification {

    def "should build space"() {
        when:
        SpaceService space = spaceFactory.buildSpaceService(Space.of("testSpace"))

        then:
        space != null
    }
}