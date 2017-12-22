package com.easydb.easydb.domain;

public interface BucketRepository {

	void create(BucketDefinition bucketDefinition);

	boolean exists(String name);

	void remove(String name);

	BucketElement insertElement(BucketElement element);

	BucketElement getElement(String bucketName, String id);

}
