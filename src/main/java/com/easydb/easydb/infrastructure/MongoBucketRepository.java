package com.easydb.easydb.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.easydb.easydb.domain.bucket.BucketOrElementDoesNotExistException;
import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.Element;
import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class MongoBucketRepository implements BucketRepository {

	private final MongoTemplate mongoTemplate;

	public MongoBucketRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public boolean exists(String name) {
		return mongoTemplate.collectionExists(name);
	}

	@Override
	public void remove(String name) {
		mongoTemplate.dropCollection(name);
	}

	@Override
	public Element insertElement(Element element) {
		mongoTemplate.insert(PersistentBucketElement.of(element), element.getBucketName());
		return element;
	}

	@Override
	public Element getElement(String bucketName, String id) {
		PersistentBucketElement elementFromDb = getPersistentElement(bucketName, id);
		return Optional.ofNullable(elementFromDb)
				.map(PersistentBucketElement::toDomainElement)
				.orElseThrow(() -> new BucketOrElementDoesNotExistException(bucketName, id));
	}

	@Override
	public void removeElement(String bucketName, String id) {
		mongoTemplate.remove(getPersistentElement(bucketName, id), bucketName);
	}

	@Override
	public boolean elementExists(String bucketName, String elementId) {
		try {
			getElement(bucketName, elementId);
			return true;
		}
		catch (BucketOrElementDoesNotExistException e) {
			return false;
		}
	}

	@Override
	public void updateElement(Element toUpdate) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(toUpdate.getId()));
		Update update = new Update();
		update.set("fields", toUpdate.getFields());
		WriteResult updateResult = mongoTemplate.updateFirst(query, update, toUpdate.getBucketName());
		if (updateResult.getN() == 0) {
			throw new BucketOrElementDoesNotExistException(toUpdate.getBucketName(), toUpdate.getId());
		}
	}

	@Override
	public List<Element> getAllElements(String name) {
		return mongoTemplate.findAll(PersistentBucketElement.class, name).stream()
				.map(PersistentBucketElement::toDomainElement)
				.collect(Collectors.toList());
	}

	private PersistentBucketElement getPersistentElement(String bucketName, String id) {
		return mongoTemplate.findById(id, PersistentBucketElement.class, bucketName);
	}
}
