package com.easydb.easydb.domain;

public class BucketDefinition {
	private final String name;

	private BucketDefinition(String name) {
		this.name = name;
	}

	public static BucketDefinition of(String name) {
		return new BucketDefinition(name);
	}

	public String getName() {
		return name;
	}
}
