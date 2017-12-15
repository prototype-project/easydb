package com.easydb.easydb.domain;

public interface BucketRepository {

	void create(BucketDefinition bucketDefinition);

	boolean exists(String name);
}
