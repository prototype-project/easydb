package com.easydb.easydb.domain;

import java.util.List;

public interface BucketRepository {

	void create(BucketDefinition bucketDefinition);

	boolean exists(String name);

	void remove(String name);

	BucketElement insertElement(BucketElement element);

	BucketElement getElement(String bucketName, String id);

	void removeElement(String bucketName, String id);

	boolean elementExists(String bucketName, String elementId);

	void updateElement(BucketElement toUpdate);

	List<BucketElement> getAllElements(String name);
}
