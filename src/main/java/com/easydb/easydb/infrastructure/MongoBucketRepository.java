package com.easydb.easydb.infrastructure;

import com.easydb.easydb.domain.BucketDefinition;
import com.easydb.easydb.domain.BucketRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

public class MongoBucketRepository implements BucketRepository {

	private final MongoTemplate mongoTemplate;

	public MongoBucketRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void create(BucketDefinition bucketDefinition) {
		mongoTemplate.createCollection(bucketDefinition.getName());
	}

	@Override
	public boolean exists(String name) {
		return mongoTemplate.collectionExists(name);
	}

	@Override
	public void remove(String name) {

	}
}
