package com.easydb.easydb.infrastructure;

import com.easydb.easydb.domain.BucketDefinition;
import com.easydb.easydb.domain.BucketDoesNotExistException;
import com.easydb.easydb.domain.BucketElement;
import com.easydb.easydb.domain.BucketExistsException;
import com.easydb.easydb.domain.BucketRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

public class MongoBucketRepository implements BucketRepository {

	private final MongoTemplate mongoTemplate;

	public MongoBucketRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void create(BucketDefinition bucketDefinition) {
		if (exists(bucketDefinition.getName())) {
			throw new BucketExistsException(bucketDefinition.getName());
		}
		mongoTemplate.createCollection(bucketDefinition.getName());
	}

	@Override
	public boolean exists(String name) {
		return mongoTemplate.collectionExists(name);
	}

	@Override
	public void remove(String name) {
		if (!exists(name)) {
			throw new BucketDoesNotExistException(name);
		}
		mongoTemplate.dropCollection(name);
	}

	@Override
	public void insertElement(BucketElement element) {
		mongoTemplate.save(element);
	}
}
