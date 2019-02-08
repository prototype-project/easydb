package com.easydb.easydb.infrastructure.bucket;

import com.easydb.easydb.config.MongoProperties;
import com.easydb.easydb.domain.bucket.BucketDoesNotExistException;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.ElementAlreadyExistsException;
import com.easydb.easydb.domain.transactions.ConcurrentTransactionDetectedException;
import com.easydb.easydb.domain.bucket.VersionedElement;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.easydb.easydb.domain.bucket.ElementDoesNotExistException;
import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.Element;
import com.mongodb.WriteResult;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class MongoBucketRepository implements BucketRepository {

    private final static String MONGO_ADMIN_DATABASE_NAME = "admin";

    private final MongoTemplate mongoTemplate;
    private final MongoClient mongoClient;
    private final MongoClient mongoAdminClient;
    private final MongoProperties mongoProperties;

    public MongoBucketRepository(MongoTemplate mongoTemplate, MongoClient mongoClient,
                                 MongoClient mongoAdminClient,
                                 MongoProperties mongoProperties) {
        this.mongoTemplate = mongoTemplate;
        this.mongoClient = mongoClient;
        this.mongoAdminClient = mongoAdminClient;
        this.mongoProperties = mongoProperties;
    }

    @Override
    public boolean bucketExists(String name) {
        return mongoTemplate.collectionExists(name);
    }

    @Override
    public void createBucket(String name) {
        mongoTemplate.createCollection(name);
    }

    @Override
    public void removeBucket(String bucketName) {
        ensureBucketExists(bucketName);
        mongoTemplate.dropCollection(bucketName);
    }

    @Override
    public void insertElement(Element element) {
        ensureBucketExists(element.getBucketName());

        try {
//            createShardIfInsertingFirstElement(element.getName());
            mongoTemplate.insert(PersistentBucketElement.of(element), element.getBucketName());
        } catch (DuplicateKeyException e) {
            throw new ElementAlreadyExistsException(
                    "Element " + element + " already exists");
        }
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
    public VersionedElement getElement(String bucketName, String id, long requiredVersion) {
        VersionedElement versionedElement = getElement(bucketName, id);
        if (versionedElement.getVersionOrThrowErrorIfEmpty() != requiredVersion) {
            throw new ConcurrentTransactionDetectedException(
                    String.format("Version of element %s changed since last read. Required version: %d", versionedElement, requiredVersion));
        }
        return versionedElement;
    }

    @Override
    public void removeElement(String bucketName, String id) {
        ensureElementExists(bucketName, id);

        mongoTemplate.remove(getPersistentElement(bucketName, id), bucketName);
    }

    @Override
    public boolean elementExists(String bucketName, String elementId) {
        try {
            getElement(bucketName, elementId);
            return true;
        } catch (ElementDoesNotExistException e) {
            return false;
        }
    }

    @Override
    public void updateElement(VersionedElement toUpdate) {
        VersionedElement element = getElement(toUpdate.getBucketName(), toUpdate.getId());

        Query query = buildUpdateQuery(toUpdate);
        Update update = new Update();
        update.set("version", element.getVersionOrThrowErrorIfEmpty() + 1);
        update.set("fields", toUpdate.getFields());

        WriteResult updateResult = mongoTemplate.updateFirst(query, update, toUpdate.getBucketName());
        validateUpdateResultAgainstConcurrency(updateResult, toUpdate);
    }

    @Override
    public List<VersionedElement> filterElements(BucketQuery query) {
        ensureBucketExists(query.getBucketName());

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

    // TODO only in sharding mongo mode - add config
    private void createShardIfInsertingFirstElement(String bucketName) {
        if (!bucketExists(bucketName)) {
           createShardedCollection(bucketName);
        }
    }

    private void createShardedCollection(String bucketName) {
        mongoClient.getDatabase(mongoProperties.getDatabaseName()).createCollection(bucketName);
        BasicDBObject shardKey = new BasicDBObject("_id", "hashed");
        BasicDBObject shardCollection = new BasicDBObject("shardCollection", mongoProperties.getDatabaseName() + "." + bucketName);
        shardCollection.put("key", shardKey);
        mongoAdminClient.getDatabase(MONGO_ADMIN_DATABASE_NAME)
                .runCommand(shardCollection);
    }

    private void ensureElementExists(String bucketName, String id) {
        getElement(bucketName, id);
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

    private Query buildUpdateQuery(VersionedElement toUpdate) {
        Query query = new Query();
        if (toUpdate.getVersion().isPresent()) {
            query.addCriteria(Criteria.where("_id").is(toUpdate.getId()).and("version").is(toUpdate.getVersion().get()));
        } else {
            query.addCriteria(Criteria.where("_id").is(toUpdate.getId()));
        }
        return query;
    }

    private void validateUpdateResultAgainstConcurrency(WriteResult writeResult, VersionedElement toUpdate) {
        if (writeResult.getN() == 0) {
            throw new ConcurrentTransactionDetectedException(
                    String.format("%s was updated by concurrent transaction", toUpdate));
        }

    }
}
