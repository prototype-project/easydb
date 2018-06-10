package com.easydb.easydb.api.bucket

import com.easydb.easydb.BaseSpec
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.space.SpaceService
import com.easydb.easydb.domain.space.Space
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.SpaceServiceFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate


class RemoveBucketSpec extends BaseSpec {
    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    SpaceServiceFactory spaceFactory

    @Autowired
    SpaceRepository spaceDefinitionRepository

    SpaceService space

    String TEST_SPACE_NAME = "testSpace"
    String TEST_BUCKET_NAME = "testBucket"

    def setup() {
        Space spaceDefinition = Space.of(TEST_SPACE_NAME)
        spaceDefinitionRepository.save(spaceDefinition)
        space = spaceFactory.buildSpaceService(spaceDefinition)
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
