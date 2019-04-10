package com.easydb.easydb.domain.bucket;

import java.util.Optional;

public class BucketSubscriptionQuery {
    private final String spaceName;
    private final String bucketName;
    private final Optional<String> query;

    private BucketSubscriptionQuery(String spaceName, String bucketName, Optional<String> query) {
        this.spaceName = spaceName;
        this.bucketName = bucketName;
        this.query = query;
    }

    public static BucketSubscriptionQuery of(String spaceName, String bucketName, Optional<String> query) {
        return new BucketSubscriptionQuery(spaceName, bucketName, query);
    }

    public String getBucketName() {
        return bucketName;
    }

    public Optional<String> getQuery() {
        return query;
    }

    public String getSpaceName() {
        return spaceName;
    }
}
