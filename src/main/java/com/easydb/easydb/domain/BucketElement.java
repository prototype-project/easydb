package com.easydb.easydb.domain;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BucketElement {

	private final String id;
	private final String bucketName;

	private final List<BucketElementField> fields;

	private BucketElement(String id, String bucketName, List<BucketElementField> fields) {
		this.id = id;
		this.bucketName = bucketName;
		this.fields = fields;
	}

	static BucketElement of(ElementCreateDto elementCreateDto) {
		return new BucketElement(
				"sda",
				elementCreateDto.getBucketName(),
				elementCreateDto.getFields().stream().map(BucketElementField::of).collect(Collectors.toList())
		);
	}

	public static BucketElement of(ElementUpdateDto elementToUpdate) {
		return new BucketElement(
				"sda",
				elementToUpdate.getBucketName(),
				elementToUpdate.getFields().stream().map(BucketElementField::of).collect(Collectors.toList())
		);
	}

	public static BucketElement of(String id, String name, List<BucketElementField> fields) {
		return new BucketElement(id, name, fields);
	}

	ElementQueryDto toQueryDto() {
		return ElementQueryDto.of(id, bucketName,
				fields.stream()
						.map(BucketElementField::toDto)
						.collect(Collectors.toList())
		);
	}

	public String getBucketName() {
		return bucketName;
	}

	public String getId() {
		return id;
	}

	public List<BucketElementField> getFields() {
		return Collections.unmodifiableList(fields);
	}
}
