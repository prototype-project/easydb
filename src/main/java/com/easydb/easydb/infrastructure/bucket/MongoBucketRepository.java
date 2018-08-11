package com.easydb.easydb.infrastructure.bucket;

import com.easydb.easydb.domain.bucket.BucketDoesNotExistException;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.VersionedElement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.easydb.easydb.domain.bucket.ElementDoesNotExistException;
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
    public boolean bucketExists(String name) {
        return mongoTemplate.collectionExists(name);
    }

    @Override
    public void removeBucket(String bucketName) {
        ensureBucketExists(bucketName);
        mongoTemplate.dropCollection(bucketName);
    }

    @Override
    public void insertElement(Element element) {
        mongoTemplate.insert(PersistentBucketElement.of(element), element.getBucketName());
    }

    @Override
    public VersionedElement getElement(String bucketName, String id) {
        ensureBucketExists(bucketName);

        PersistentBucketElement elementFromDb = getPersistentElement(bucketName, id);
        return Optional.ofNullable(elementFromDb)
                .map(it -> it.toDomainVersionedElement(bucketName))
                .orElseThrow(() -> new ElementDoesNotExistException(bucketName, id));
    }

    @Override
    public void removeElement(String bucketName, String id) {
        ensureBucketExists(bucketName);

        mongoTemplate.remove(getPersistentElement(bucketName, id), bucketName);
    }

    @Override
    public boolean elementExists(String bucketName, String elementId) {
        ensureBucketExists(bucketName);

        try {
            getElement(bucketName, elementId);
            return true;
        } catch (ElementDoesNotExistException e) {
            return false;
        }
    }

    @Override
    public void updateElement(Element toUpdate) {
        ensureBucketExists(toUpdate.getBucketName());

        PersistentBucketElement persistedElement = getPersistentElement(toUpdate.getBucketName(), toUpdate.getId());
        if (persistedElement == null) {
            throw new ElementDoesNotExistException(toUpdate.getBucketName(), toUpdate.getId());
        }
        PersistentBucketElement persistentUpdated = PersistentBucketElement.of(
                toUpdate.getId(), toUpdate.getFields(), persistedElement.getVersion());
        mongoTemplate.save(persistentUpdated);
    }

    @Override
    public List<Element> filterElements(BucketQuery query) {
        Query mongoQuery = fromBucketQuery(query);
        return mongoTemplate.find(mongoQuery, PersistentBucketElement.class, query.getBucketName()).stream()
                .map(it -> it.toDomainVersionedElement(query.getBucketName()))
                .collect(Collectors.toList());
    }

    @Override
    public long getNumberOfElements(String bucketName) {
        ensureBucketExists(bucketName);

        return mongoTemplate.count(new Query(), PersistentBucketElement.class, bucketName);
    }

    private void ensureBucketExists(String bucketName) {
        if (!bucketExists(bucketName)) {
            throw new BucketDoesNotExistException(bucketName);
        }
    }

    private PersistentBucketElement getPersistentElement(String bucketName, String id) {
        return mongoTemplate.findById(id, PersistentBucketElement.class, bucketName);
    }

    private Query fromBucketQuery(BucketQuery bucketQuery) {
        Query query = new Query();
        query.limit(bucketQuery.getLimit());
        query.skip(bucketQuery.getOffset());
        return query;
    }
}
