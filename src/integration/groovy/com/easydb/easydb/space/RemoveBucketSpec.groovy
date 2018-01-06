package com.easydb.easydb.space

import com.easydb.easydb.BaseSpec
import com.easydb.easydb.domain.ElementTestBuilder
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.space.Space
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate


class RemoveBucketSpec extends BaseSpec {
    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    Space space

    def "should remove bucket"() {
        given:
        Element toCreate = ElementTestBuilder.builder().build()
        space.addElement(toCreate)

        when:
        restTemplate.delete(localUrl('/api/v1/buckets/people'))

        then:
        !space.bucketExists('people')
    }
}
