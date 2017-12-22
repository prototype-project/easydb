package com.easydb.easydb.domain;

class BucketElementField {

	private final String name;
	private final String value;

	private BucketElementField(String name, String value) {
		this.name = name;
		this.value = value;
	}

	static BucketElementField of(ElementFieldDto elementFieldDto) {
		return new BucketElementField(elementFieldDto.getName(), elementFieldDto.getValue());
	}

	ElementFieldDto toDto() {
		return ElementFieldDto.of(name, value);
	}
}
