package com.easydb.easydb.domain;


import java.util.List;
import java.util.stream.Collectors;

public class ElementCreateDto {

	private final String bucketName;
	private final List<ElementFieldDto> fields;

	private ElementCreateDto(String bucketName, List<ElementFieldDto> fields) {
		this.bucketName = bucketName;
		this.fields = fields;
	}

	static ElementCreateDto of(String bucketName, List<ElementFieldDto> fields) {
		return new ElementCreateDto(bucketName, fields);
	}

	BucketElement toDomainElement() {
		return BucketElement.of(
				bucketName,
				fields.stream()
						.map(ElementFieldDto::toDomainField)
						.collect(Collectors.toList())
		);
	}
}
