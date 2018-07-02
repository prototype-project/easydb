package com.easydb.easydb.api.bucket

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.TestUtils
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.space.SpaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate


class RemoveBucketSpec extends BaseIntegrationSpec implements TestUtils {

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    SpaceService spaceService

    String spaceName
    BucketService bucketService

    def setup() {
        spaceName = addSampleSpace().body.spaceName
        bucketService = spaceService.bucketServiceForSpace(spaceName)
    }

    def "should remove bucket"() {
        given:
        Element toCreate = ElementTestBuilder.builder().build()
        bucketService.addElement(toCreate)

        when:
        restTemplate.delete(localUrl('/api/v1/' + spaceName + '/' + toCreate.bucketName))

        then:
        !bucketService.bucketExists(toCreate.bucketName)
    }

    def "should return 404 when trying to remove not existing bucket"() {
        when:
        restTemplate.delete(localUrl('/api/v1/' + spaceName + '/notExisting'))

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.rawStatusCode == 404
    }
}
