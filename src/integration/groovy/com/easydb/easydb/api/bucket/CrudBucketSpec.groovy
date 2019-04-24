package com.easydb.easydb.api.bucket

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.TestHttpOperations
import com.easydb.easydb.domain.bucket.BucketName
import com.easydb.easydb.domain.bucket.BucketService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate


class CrudBucketSpec extends BaseIntegrationSpec implements TestHttpOperations {

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    BucketService bucketService

    String spaceName

    def setup() {
        spaceName = addSampleSpace().body.spaceName
    }

    def "should create bucket"() {
        when:
        def response = createBucket(spaceName, "exampleBucket")

        then:
        bucketService.bucketExists(new BucketName(spaceName, "exampleBucket"))

        and:
        response.getStatusCode() == HttpStatus.CREATED
    }

    def "should throw error when trying to create already existing bucket"() {
        given:
        createBucket(spaceName, "existingBucket")

        when:
        createBucket(spaceName, "existingBucket")

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should remove bucket"() {
        given:
        createBucket(spaceName, "bucketToRemove")

        when:
        deleteBucket(spaceName, "bucketToRemove")

        then:
        !bucketService.bucketExists(new BucketName(spaceName, "bucketToRemove"))
    }

    def "should return 404 when trying to remove not existing bucket"() {
        when:
        restTemplate.delete(buildBucketUrl(spaceName, "notExisting"))

        then:
        HttpClientErrorException ex = thrown()

        and:
        ex.statusCode == HttpStatus.NOT_FOUND
    }
}
