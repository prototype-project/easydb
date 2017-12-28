package com.easydb.easydb.domain.bucket.dto;

public class ElementFieldDto {

	private final String name;
	private final String value;

	private ElementFieldDto(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static ElementFieldDto of(String name, String value) {
		return new ElementFieldDto(name, value);
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}
}
