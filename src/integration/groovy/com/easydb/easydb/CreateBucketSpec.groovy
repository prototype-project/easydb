package com.easydb.easydb

import com.easydb.easydb.domain.Space
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@ContextConfiguration(classes = [SpaceTestConfig])
class CreateBucketSpec extends BaseSpec {
    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    Space space

    def setup() {
        space.removeBucket('testBucket')
    }

    def "should create bucket"() {
        given:
        String bucketDefinition = sampleBucketDefinition()

        when:
        ResponseEntity response = restTemplate.exchange(localUrl('/api/v1/buckets'),
                HttpMethod.POST, httpJsonEntity(bucketDefinition), Void.class)

        then:
        space.bucketExists('testBucket')

        and:
        response.statusCodeValue == 201
    }

    def "should return error when trying to create bucket with non unique name"() {
        given:
        String bucketDefinition = sampleBucketDefinition()
        restTemplate.exchange(localUrl('/api/v1/buckets'),
                HttpMethod.POST, httpJsonEntity(bucketDefinition), Void.class)

        when:
        restTemplate.exchange(localUrl('/api/v1/buckets'),
                HttpMethod.POST, httpJsonEntity(bucketDefinition), Void.class)

        then:
        thrown HttpClientErrorException
    }

    String sampleBucketDefinition() {
        JsonOutput.toJson([
                name: 'testBucket',
                fields: ['field1', 'field2']
        ])
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