package com.easydb.easydb.domain.bucket;


import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ElementField that = (ElementField) o;

		return name.equals(that.name) &&
				value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value);
	}
}