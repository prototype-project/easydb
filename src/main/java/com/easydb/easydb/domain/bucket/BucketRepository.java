package com.easydb.easydb.domain.bucket;

import java.util.List;

public interface BucketRepository {

	boolean bucketExists(String name);

	void removeBucket(String name);

	void insertElement(Element element);

	Element getElement(String bucketName, String id) throws ElementDoesNotExistException;

	void removeElement(String bucketName, String id);

	boolean elementExists(String bucketName, String elementId);

	void updateElement(Element toUpdate) throws ElementDoesNotExistException;

	List<Element> filterElements(BucketQuery query);

	long getNumberOfElements(String bucketName);
}
