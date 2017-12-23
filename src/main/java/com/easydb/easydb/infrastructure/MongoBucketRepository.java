package com.easydb.easydb.infrastructure;

import com.easydb.easydb.domain.BucketDefinition;
import com.easydb.easydb.domain.BucketDoesNotExistException;
import com.easydb.easydb.domain.BucketElement;
import com.easydb.easydb.domain.BucketElementDoesNotExistException;
import com.easydb.easydb.domain.BucketExistsException;
import com.easydb.easydb.domain.BucketRepository;
import com.easydb.easydb.domain.ElementUpdateDto;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
	public BucketElement insertElement(BucketElement element) {
		mongoTemplate.insert(PersistentBucketElement.of(element), element.getBucketName());
		return element;
	}

	@Override
	public BucketElement getElement(String bucketName, String id) {
		PersistentBucketElement elementFromDb = getPersistentElement(bucketName, id);
		return Optional.ofNullable(elementFromDb)
				.map(PersistentBucketElement::toDomainElement)
				.orElseThrow(() -> new BucketElementDoesNotExistException(bucketName, id));
	}

	@Override
	public void removeElement(String bucketName, String id) {
		if (!elementExists(bucketName, id)) {
			throw new BucketElementDoesNotExistException(bucketName, id);
		}
		mongoTemplate.remove(getPersistentElement(bucketName, id), bucketName);
	}

	@Override
	public boolean elementExists(String bucketName, String elementId) {
		try {
			getElement(bucketName, elementId);
			return true;
		}
		catch (BucketElementDoesNotExistException e) {
			return false;
		}
	}

	@Override
	public void updateElement(BucketElement toUpdate) {
		if (!elementExists(toUpdate.getBucketName(), toUpdate.getId())) {
			throw new BucketElementDoesNotExistException(toUpdate.getBucketName(), toUpdate.getId());
		}
		mongoTemplate.save(PersistentBucketElement.of(toUpdate), toUpdate.getBucketName());
	}

	@Override
	public List<BucketElement> getAllElements(String name) {
		return mongoTemplate.findAll(PersistentBucketElement.class, name).stream()
				.map(PersistentBucketElement::toDomainElement)
				.collect(Collectors.toList());
	}

	private PersistentBucketElement getPersistentElement(String bucketName, String id) {
		return mongoTemplate.findById(id, PersistentBucketElement.class, bucketName);
	}

}
