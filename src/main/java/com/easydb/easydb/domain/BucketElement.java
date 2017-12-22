package com.easydb.easydb.domain;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


//TODO make dedicated persistent class
@Document
public class BucketElement {

	@Id
	private final String id;
	private final String name;
	private final List<BucketElementField> fields;

	private BucketElement(String id, String name, List<BucketElementField> fields) {
		this.id = id;
		this.name = name;
		this.fields = fields;
	}

	static BucketElement of(ElementCreateDto elementCreateDto) {
		return new BucketElement(
				"sda",
				elementCreateDto.getName(),
				elementCreateDto.getFields().stream().map(BucketElementField::of).collect(Collectors.toList())
		);
	}

	ElementQueryDto toQueryDto() {
		return ElementQueryDto.of(id, name,
				fields.stream()
						.map(BucketElementField::toDto)
						.collect(Collectors.toList())
		);
	}

	public String getName() {
		return name;
	}
}
