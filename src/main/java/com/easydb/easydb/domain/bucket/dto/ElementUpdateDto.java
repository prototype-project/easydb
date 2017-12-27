package com.easydb.easydb.domain.bucket.dto;

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

	public String getBucketName() {
		return bucketName;
	}

	public String getElementId() {
		return elementId;
	}

	public List<ElementFieldDto> getFields() {
		return fields;
	}
}