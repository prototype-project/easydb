package com.easydb.easydb.domain;


public class ElementCreateDto {

	private final String bucketName;
	private final ElementCreateFieldDto [] fields;

	private ElementCreateDto(String bucketName, ElementCreateFieldDto[] fields) {
		this.bucketName = bucketName;
		this.fields = fields;
	}

	public static ElementCreateDto of(String bucketName, ElementCreateFieldDto... fields) {
		return new ElementCreateDto(bucketName, fields);
	}

	public static to
}
