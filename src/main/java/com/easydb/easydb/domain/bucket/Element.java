package com.easydb.easydb.domain.bucket;

import com.easydb.easydb.domain.bucket.dto.ElementCreateDto;
import com.easydb.easydb.domain.bucket.dto.ElementQueryDto;
import com.easydb.easydb.domain.bucket.dto.ElementUpdateDto;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Element {

	private final String id;
	private final String bucketName;
	private final List<ElementField> fields;

	private Element(String id, String bucketName, List<ElementField> fields) {
		this.id = id;
		this.bucketName = bucketName;
		this.fields = ImmutableList.copyOf(fields);
	}

	public static Element of(String id, ElementCreateDto elementCreateDto) {
		return new Element(
				id,
				elementCreateDto.getBucketName(),
				elementCreateDto.getFields().stream().map(ElementField::of).collect(Collectors.toList())
		);
	}

	public static Element of(ElementUpdateDto elementToUpdate) {
		return new Element(
				elementToUpdate.getElementId(),
				elementToUpdate.getBucketName(),
				elementToUpdate.getFields().stream().map(ElementField::of).collect(Collectors.toList())
		);
	}

	public static Element of(String id, String name, List<ElementField> fields) {
		return new Element(id, name, fields);
	}

	public ElementQueryDto toQueryDto() {
		return ElementQueryDto.of(id, bucketName,
				fields.stream()
						.map(ElementField::toDto)
						.collect(Collectors.toList())
		);
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
}
