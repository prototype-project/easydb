package com.easydb.easydb.domain.bucket;


import java.util.Optional;

public class BucketQuery {

    private final String bucketName;
    private final int limit;
    private final int offset;
    private final Optional<String> query;

    private BucketQuery(String bucketName, int limit, int offset, Optional<String> query) {
        this.bucketName = bucketName;
        this.limit = limit;
        this.offset = offset;
        this.query = query;
        validateConstraints();
    }

    public String getBucketName() {
        return bucketName;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public Optional<String> getQuery() {
        return query;
    }

    public static BucketQuery of(String name, int limit, int offset, Optional<String> query) {
        return new BucketQuery(name, limit, offset, query);
    }

    public static BucketQuery of(String name, int limit, int offset) {
        return new BucketQuery(name, limit, offset, Optional.empty());
    }

    private void validateConstraints() {
        if (limit <= 0 || offset < 0) {
            throw new InvalidPaginationDataException();
        }
    }
}