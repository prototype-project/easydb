package com.easydb.easydb.domain.bucket;

import java.util.Optional;

public class BucketSubscriptionQuery {
    private final BucketName bucketName;
    private final Optional<String> query;

    private BucketSubscriptionQuery(BucketName bucketName, Optional<String> query) {
        this.bucketName = bucketName;
        this.query = query;
    }

    public static BucketSubscriptionQuery of(BucketName bucketName, Optional<String> query) {
        return new BucketSubscriptionQuery(bucketName, query);
    }

    public BucketName getBucketName() {
        return bucketName;
    }

    public Optional<String> getQuery() {
        return query;
    }
}
