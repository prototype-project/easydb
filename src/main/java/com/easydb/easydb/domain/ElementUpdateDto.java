package com.easydb.easydb.domain;

import java.util.List;

public class ElementUpdateDto {

	private final String bucketName;
	private final String elementId;
	private final List<ElementFieldDto> fields;

	private ElementUpdateDto(String bucketName, String elementId, List<ElementFieldDto> fields) {
		this.bucketName = bucketName;
		this.elementId = elementId;
		this.fields = fields;
	}

	public static ElementUpdateDto of(String bucketName, String elementId, List<ElementFieldDto> fields) {
		return new ElementUpdateDto(bucketName, elementId, fields);
	}

	String getBucketName() {
		return bucketName;
	}

	String getElementId() {
		return elementId;
	}

	List<ElementFieldDto> getFields() {
		return fields;
	}
}
