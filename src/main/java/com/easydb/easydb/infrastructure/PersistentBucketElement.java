package com.easydb.easydb.infrastructure;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementField;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
class PersistentBucketElement {

	@Id
	private final String id;
	private final String bucketName; // TODO remove it
	private final List<ElementField> fields;

	private PersistentBucketElement(String id, String bucketName, List<ElementField> fields) {
		this.id = id;
		this.bucketName = bucketName;
		this.fields = fields;
	}

	static PersistentBucketElement of(Element element) {
		return new PersistentBucketElement(element.getId(),
				element.getBucketName(), element.getFields());
	}

	Element toDomainElement() {
		return Element.of(id, bucketName, fields);
	}
}

