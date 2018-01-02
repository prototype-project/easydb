package com.easydb.easydb.domain.space;

import com.easydb.easydb.domain.bucket.dto.ElementCreateDto;
import com.easydb.easydb.domain.bucket.dto.ElementQueryDto;
import com.easydb.easydb.domain.bucket.dto.ElementUpdateDto;

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
