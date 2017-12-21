package com.easydb.easydb.domain;

class BucketElementField {

	private final String name;
	private final String value;

	private BucketElementField(String name, String value) {
		this.name = name;
		this.value = value;
	}

	static BucketElementField of(String name, String value) {
		return new BucketElementField(name, value);
	}

	String getName() {
		return name;
	}

	String getValue() {
		return value;
	}
}
