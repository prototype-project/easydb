package com.easydb.easydb.domain.bucket;


public class BucketQuery {

    private final String bucketName;
    private final int limit;
    private final int offset;

    private BucketQuery(String bucketName, int limit, int offset) {
        this.bucketName = bucketName;
        this.limit = limit;
        this.offset = offset;
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

    public static BucketQuery of(String name, int limit, int offset) {
        return new BucketQuery(name, limit, offset);
    }

    private void validateConstraints() {
        if (limit <= 0 || offset < 0) {
            throw new InvalidPaginationDataException();
        }
    }

}