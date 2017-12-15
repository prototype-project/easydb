package com.easydb.easydb.infrastructure;

import com.easydb.easydb.domain.BucketDefinition;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
class PersistentBucket {
	private String name;
	private List<String> fields;

	private PersistentBucket(String name, List<String> fields) {
		this.name = name;
		this.fields = fields;
	}

	static PersistentBucket of(BucketDefinition bucketDefinition) {
		return new PersistentBucket(bucketDefinition.getName(), bucketDefinition.getFields());
	}

	BucketDefinition toDomain() {
		return BucketDefinition.of(name, fields);
	}
}
