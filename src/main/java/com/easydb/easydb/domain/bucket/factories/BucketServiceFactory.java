package com.easydb.easydb.domain.bucket.factories;

import com.easydb.easydb.domain.bucket.BucketService;

public interface BucketServiceFactory {
    BucketService buildBucketService(String spaceName);
}
