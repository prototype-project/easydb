package com.easydb.easydb

import com.easydb.easydb.domain.Space
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@ContextConfiguration(classes = [SpaceTestConfig])
class RemoveBucketSpec extends BaseSpec {
    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    Space space

    def "should remove bucket"() {
        given:
        sampleBucket()

        when:
        restTemplate.delete(localUrl('/api/v1/buckets/testBucket'))

        then:
        !space.bucketExists('testBucket')
    }

    def "should throw error when trying to remove nonexistent bucket"() {
        when:
        restTemplate.delete(localUrl('/api/v1/buckets/testBucket'))

        then:
        thrown HttpClientErrorException
    }
}
