package com.easydb.easydb.domain;

import java.util.Collections;
import java.util.List;

public class BucketDefinition {
	private final String name;
	private final List<String> fields;

	private BucketDefinition(String name, List<String> fields) {
		this.name = name;
		this.fields = fields;
	}

	public static BucketDefinition of(String name, List<String> fields) {
		return new BucketDefinition(name, fields);
	}

	public String getName() {
		return name;
	}

	public List<String> getFields() {
		return Collections.unmodifiableList(fields);
	}
}
