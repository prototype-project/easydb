package com.easydb.easydb.domain;

import java.util.List;

public interface Space {
    void createBucket(String name, List<String> fields);

    boolean bucketExists(String name);

    void removeBucket(String name);
}
