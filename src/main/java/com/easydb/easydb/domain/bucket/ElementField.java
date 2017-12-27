package com.easydb.easydb.domain.bucket;

import com.easydb.easydb.domain.bucket.dto.ElementFieldDto;

public class ElementField {

	private final String name;
	private final String value;

	private ElementField(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static ElementField of(ElementFieldDto elementFieldDto) {
		return new ElementField(elementFieldDto.getName(), elementFieldDto.getValue());
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public ElementFieldDto toDto() {
		return ElementFieldDto.of(name, value);
	}
}