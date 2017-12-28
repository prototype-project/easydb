package com.easydb.easydb.domain.bucket.dto;


import java.util.List;

public class ElementCreateDto {

	private final String bucketName;
	private final List<ElementFieldDto> fields;

	private ElementCreateDto(String bucketName, List<ElementFieldDto> fields) {
		this.bucketName = bucketName;
		this.fields = fields;
	}

	public static ElementCreateDto of(String bucketName, List<ElementFieldDto> fields) {
		return new ElementCreateDto(bucketName, fields);
	}

	public String getBucketName() {
		return bucketName;
	}

	public List<ElementFieldDto> getFields() {
		return fields;
	}
}
