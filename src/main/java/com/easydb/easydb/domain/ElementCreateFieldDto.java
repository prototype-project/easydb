package com.easydb.easydb.domain;

public class ElementCreateFieldDto {

	private final String name;
	private final String value;

	private ElementCreateFieldDto(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static ElementCreateFieldDto create(String name, String value) {
		return new ElementCreateFieldDto(name, value);
	}
}
