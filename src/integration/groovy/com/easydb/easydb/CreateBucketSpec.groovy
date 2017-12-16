package com.easydb.easydb

import com.easydb.easydb.domain.Space
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate

@ContextConfiguration(classes = [SpaceTestConfig])
class CreateBucketSpec extends BaseSpec {
    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    Space space

    def "should create bucket"() {
        given:
        String bucketDefinition = JsonOutput.toJson([
                name: 'testBucket',
                fields: ['field1', 'field2']
        ])

        when:
        restTemplate.exchange(localUrl('/api/v1/buckets'),
                HttpMethod.POST, httpJsonEntity(bucketDefinition), Void.class)


        then:
        space.bucketExists('testBucket')
    }

    HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8)
        headers
    }

    HttpEntity httpJsonEntity(String jsonBody) {
        new HttpEntity<String>(jsonBody, headers())
    }
}