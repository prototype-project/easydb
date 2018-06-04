package com.easydb.easydb.infrastructure.bucket;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementField;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
class PersistentBucketElement {

	@Id
	private final String id;
	private final List<ElementField> fields;

	private PersistentBucketElement(String id, List<ElementField> fields) {
		this.id = id;
		this.fields = fields;
	}

	static PersistentBucketElement of(Element element) {
		return new PersistentBucketElement(element.getId(), element.getFields());
	}

	Element toDomainElement(String bucketName) {
		return Element.of(id, bucketName, fields);
	}
}

