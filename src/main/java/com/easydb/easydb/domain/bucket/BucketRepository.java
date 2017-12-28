package com.easydb.easydb.domain.bucket;

import java.util.List;

public interface BucketRepository {

	boolean exists(String name);

	void remove(String name);

	Element insertElement(Element element);

	Element getElement(String bucketName, String id);

	void removeElement(String bucketName, String id);

	boolean elementExists(String bucketName, String elementId);

	void updateElement(Element toUpdate);

	List<Element> getAllElements(String name);
}
