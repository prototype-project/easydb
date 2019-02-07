package com.easydb.easydb.domain.bucket;

public class BucketAlreadyExistsException extends RuntimeException {
    BucketAlreadyExistsException(String bucketName) {
        super("Bucket " + bucketName + " already exists.");
    }
}
