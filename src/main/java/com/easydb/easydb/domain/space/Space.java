package com.easydb.easydb.domain.space;

import com.easydb.easydb.domain.bucket.Element;

import java.util.List;

public interface Space {
    boolean bucketExists(String name);

    void removeBucket(String name);

	void addElement(Element element);

	Element getElement(String bucketName, String id);

	void removeElement(String bucketName, String elementId);

	boolean elementExists(String bucketName, String elementId);

	void updateElement(Element toUpdate);

	List<Element> getAllElements(String name);
}
