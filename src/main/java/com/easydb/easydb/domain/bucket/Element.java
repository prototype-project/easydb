package com.easydb.easydb.domain.bucket;

import com.google.common.collect.ImmutableList;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Element {

	private final String id;
	private final String bucketName;
	private final List<ElementField> fields;
	private final Map<String, ElementField> fieldsAsMap;

	private Element(String id, String bucketName, List<ElementField> fields) {
		this.id = id;
		this.bucketName = bucketName;
		this.fields = ImmutableList.copyOf(fields);
		this.fieldsAsMap = ImmutableMap.copyOf(fields.stream().collect(
				Collectors.toMap(ElementField::getName, it -> it)));
	}


	public static Element of(String id, String name, List<ElementField> fields) {
		return new Element(id, name, fields);
	}

	public String getBucketName() {
		return bucketName;
	}

	public String getId() {
		return id;
	}

	public List<ElementField> getFields() {
		return fields;
	}

	public String getFieldValue(String fieldName) {
		return fieldsAsMap.get(fieldName).getValue();
	}
}
