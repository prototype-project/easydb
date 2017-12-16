package com.easydb.easydb

import com.easydb.easydb.domain.Space
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpClientErrorException

@ContextConfiguration(classes = [SpaceTestConfig])
class CreateBucketSpec extends BaseSpec {
    @Autowired
    Space space

    def cleanup() {
        space.removeBucket('testBucket')
    }

    def "should create bucket"() {
        when:
        ResponseEntity response = sampleBucket()

        then:
        space.bucketExists('testBucket')

        and:
        response.statusCodeValue == 201
    }

    def "should return error when trying to create bucket with non unique name"() {
        given:
        sampleBucket()

        when:
        sampleBucket()

        then:
        thrown HttpClientErrorException
    }
}