package com.easydb.easydb.domain.bucket;

public class NamesResolver {
    public static String resolve(String spaceName, String bucketName) {
        return spaceName + ":" + bucketName;
    }
}
