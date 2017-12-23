package com.easydb.easydb.infrastructure;

import com.easydb.easydb.domain.BucketElement;
import com.easydb.easydb.domain.BucketElementField;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
class PersistentBucketElement {

	@Id
	private final String id;
	private final String bucketName;
	private final List<BucketElementField> fields;

	private PersistentBucketElement(String id, String bucketName, List<BucketElementField> fields) {
		this.id = id;
		this.bucketName = bucketName;
		this.fields = fields;
	}

	static PersistentBucketElement of(BucketElement element) {
		return new PersistentBucketElement(element.getId(),
				element.getBucketName(), element.getFields());
	}

	BucketElement toDomainElement() {
		return BucketElement.of(id, bucketName, fields);
	}
}

