package com.easydb.easydb.domain.bucket;

public class NamesResolver {
    public static String resolve(BucketName bucketName) {
        return bucketName.getSpaceName()+ ":" + bucketName.getName();
    }
}
