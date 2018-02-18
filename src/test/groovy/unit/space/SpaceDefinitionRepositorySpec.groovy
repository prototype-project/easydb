package unit.space

import com.easydb.easydb.domain.space.SpaceDefinition
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
        spaceRepository.save(SpaceDefinition.of(TEST_SPACE))

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
        spaceRepository.save(SpaceDefinition.of(TEST_SPACE))

        when:
        spaceRepository.remove(TEST_SPACE)

        then:
        !spaceRepository.exists(TEST_SPACE)
    }

    def "should return space definition"() {
        given:
        spaceRepository.save(SpaceDefinition.of(TEST_SPACE))

        when:
        SpaceDefinition spaceDefinition = spaceRepository.get(TEST_SPACE)

        then:
        spaceDefinition.spaceName == TEST_SPACE
    }

    def "should return error when space name is not unique"() {
        given:
        spaceRepository.save(SpaceDefinition.of(TEST_SPACE))

        when:
        spaceRepository.save(SpaceDefinition.of(TEST_SPACE))

        then:
        thrown SpaceNameNotUniqueException
    }
}
