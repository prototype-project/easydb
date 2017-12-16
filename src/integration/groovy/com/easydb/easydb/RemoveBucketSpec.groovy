package com.easydb.easydb

import com.easydb.easydb.domain.Space
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
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
}
