package com.easydb.easydb.domain.bucket;


public class ElementField {

	private final String name;
	private final String value;

	private ElementField(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static ElementField of(String name, String value) {
		return new ElementField(name, value);
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}