package com.easydb.easydb.domain;

import java.util.List;

public class BucketElement {
	private final String id;
	private final String name;
	private final List<BucketElementField> fields;

	private BucketElement(String name, List<BucketElementField> fields) {
		this.name = name;
		this.fields = fields;
	}

	static BucketElement of(String name, List<BucketElementField> fields) {
		return new BucketElement(name, fields);
	}


	String getName() {
		return name;
	}

	List<BucketElementField> getFields() {
		return fields;
	}
}
