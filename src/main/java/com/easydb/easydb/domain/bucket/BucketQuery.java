package com.easydb.easydb.domain.bucket;


public class BucketQuery {

    private final String bucketName;
    private final int limit;
    private final int offset;
    private final String query;

    private BucketQuery(String bucketName, int limit, int offset, String query) {
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

    public String getQuery() {
        return query;
    }

    public static BucketQuery of(String name, int limit, int offset, String query) {
        return new BucketQuery(name, limit, offset, query);
    }

    private void validateConstraints() {
        if (limit <= 0 || offset < 0) {
            throw new InvalidPaginationDataException();
        }
    }
}