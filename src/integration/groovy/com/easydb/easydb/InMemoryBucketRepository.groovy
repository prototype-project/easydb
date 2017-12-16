package com.easydb.easydb;

import com.easydb.easydb.domain.BucketDefinition
import com.easydb.easydb.domain.BucketExistsException
import com.easydb.easydb.domain.BucketRepository;

import java.util.Map;

class InMemoryBucketRepository implements BucketRepository {
    Map<String, BucketDefinition> buckets = [:]

    @Override
    void create(BucketDefinition bucketDefinition) {
        if (exists(bucketDefinition.name))
            throw new BucketExistsException()
        buckets.put(bucketDefinition.name, BucketDefinition.of(bucketDefinition.getName(), bucketDefinition.getFields()))
    }

    @Override
    boolean exists(String name) {
        return buckets.containsKey(name)
    }

    @Override
    void remove(String name) {
        buckets.remove(name)
    }
}
