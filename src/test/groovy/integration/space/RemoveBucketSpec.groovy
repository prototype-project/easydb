package integration.space

import integration.BaseSpec
import unit.ElementTestBuilder
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceDefinition
import com.easydb.easydb.domain.space.SpaceDefinitionRepository
import com.easydb.easydb.domain.space.SpaceFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate


class RemoveBucketSpec extends BaseSpec {
    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    SpaceFactory spaceFactory

    @Autowired
    SpaceDefinitionRepository spaceDefinitionRepository

    Space space

    String TEST_SPACE_NAME = "testSpace"
    String TEST_BUCKET_NAME = "testBucket"

    def setup() {
        SpaceDefinition spaceDefinition = SpaceDefinition.of(TEST_SPACE_NAME)
        spaceDefinitionRepository.save(spaceDefinition)
        space = spaceFactory.buildSpace(spaceDefinition)
    }

    def cleanup() {
        spaceDefinitionRepository.remove(TEST_SPACE_NAME)
    }

    def "should remove bucket"() {
        given:
        Element toCreate = ElementTestBuilder.builder().build()
        space.addElement(toCreate)

        when:
        restTemplate.delete(localUrl('/api/v1/' + TEST_SPACE_NAME + '/'+ TEST_BUCKET_NAME))

        then:
        !space.bucketExists(TEST_BUCKET_NAME)
    }
}
