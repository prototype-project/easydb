package com.easydb.easydb.domain.bucket;

public interface BucketServiceFactory {
    BucketService buildBucketService(String spaceName);
}
