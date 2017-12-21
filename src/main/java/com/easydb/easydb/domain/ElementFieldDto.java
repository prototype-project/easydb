package com.easydb.easydb.domain;

public class ElementFieldDto {

	private final String name;
	private final String value;

	private ElementFieldDto(String name, String value) {
		this.name = name;
		this.value = value;
	}

	static ElementFieldDto of(String name, String value) {
		return new ElementFieldDto(name, value);
	}

	BucketElementField toDomainField() {
		return BucketElementField.of(name, value);
	}
}
