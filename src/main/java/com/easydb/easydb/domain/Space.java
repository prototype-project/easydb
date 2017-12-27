package com.easydb.easydb.domain;

import java.util.List;

public interface Space {
    boolean bucketExists(String name);

    void removeBucket(String name);

	ElementQueryDto addElement(ElementCreateDto element);

	ElementQueryDto getElement(String bucketName, String id);

	void removeElement(String bucketName, String elementId);

	boolean elementExists(String bucketName, String elementId);

	void updateElement(ElementUpdateDto toUpdate);

	List<ElementQueryDto> getAllElements(String name);
}
