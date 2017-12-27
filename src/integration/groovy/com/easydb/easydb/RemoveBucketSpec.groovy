package com.easydb.easydb

import com.easydb.easydb.domain.ElementCreateDto
import com.easydb.easydb.domain.ElementFieldDto
import com.easydb.easydb.domain.Space
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate

class RemoveBucketSpec extends BaseSpec {
    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    Space space

    def "should remove bucket"() {
        given:
        space.addElement(ElementCreateDto.of("people", [
                ElementFieldDto.of("firstName", "John")
        ]))

        when:
        restTemplate.delete(localUrl('/api/v1/buckets/testBucket'))

        then:
        !space.bucketExists('testBucket')
    }
}
