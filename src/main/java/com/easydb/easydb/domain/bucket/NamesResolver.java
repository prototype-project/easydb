package com.easydb.easydb.domain.bucket;

import com.easydb.easydb.domain.BucketName;

public class NamesResolver {
    public static String resolve(BucketName bucketName) {
        return bucketName.getSpaceName()+ ":" + bucketName.getName();
    }
}
