package com.easydb.easydb.domain;


import java.util.List;

public class ElementCreateDto {

	private final String bucketName;
	private final List<ElementFieldDto> fields;

	private ElementCreateDto(String bucketName, List<ElementFieldDto> fields) {
		this.bucketName = bucketName;
		this.fields = fields;
	}

	static ElementCreateDto of(String bucketName, List<ElementFieldDto> fields) {
		return new ElementCreateDto(bucketName, fields);
	}

	String getBucketName() {
		return bucketName;
	}

	List<ElementFieldDto> getFields() {
		return fields;
	}
}
