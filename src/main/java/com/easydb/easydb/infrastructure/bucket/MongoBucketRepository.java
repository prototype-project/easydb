package com.easydb.easydb.infrastructure.bucket;

import com.easydb.easydb.config.MongoProperties;
import com.easydb.easydb.domain.bucket.BucketName;
import com.easydb.easydb.domain.bucket.BucketDoesNotExistException;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.ElementAlreadyExistsException;
import com.easydb.easydb.domain.bucket.NamesResolver;
import com.easydb.easydb.domain.transactions.ConcurrentTransactionDetectedException;
import com.easydb.easydb.domain.bucket.transactions.VersionedElement;
import com.easydb.easydb.infrastructure.bucket.graphql.GraphQlElementsFetcher;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import java.util.List;
import java.util.Optional;

import com.easydb.easydb.domain.bucket.ElementDoesNotExistException;
import com.easydb.easydb.domain.bucket.transactions.BucketRepository;
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
    private final GraphQlElementsFetcher graphQlElementsFetcher;

    public MongoBucketRepository(MongoTemplate mongoTemplate, MongoClient mongoClient,
                                 MongoClient mongoAdminClient,
                                 MongoProperties mongoProperties,
                                 GraphQlElementsFetcher graphQlElementsFetcher) {
        this.mongoTemplate = mongoTemplate;
        this.mongoClient = mongoClient;
        this.mongoAdminClient = mongoAdminClient;
        this.mongoProperties = mongoProperties;
        this.graphQlElementsFetcher = graphQlElementsFetcher;
    }

    @Override
    public boolean bucketExists(BucketName name) {
        return mongoTemplate.collectionExists(NamesResolver.resolve(name));
    }

    @Override
    public void createBucket(BucketName name) {
        createShardedCollection(name);
        mongoTemplate.createCollection(NamesResolver.resolve(name));
    }

    @Override
    public void removeBucket(BucketName bucketName) {
        ensureBucketExists(bucketName);
        mongoTemplate.dropCollection(NamesResolver.resolve(bucketName));
    }

    @Override
    public void insertElement(Element element) {
        ensureBucketExists(element.getBucketName());

        try {
            mongoTemplate.insert(PersistentBucketElement.of(element), NamesResolver.resolve(element.getBucketName()));
        } catch (DuplicateKeyException e) {
            throw new ElementAlreadyExistsException(
                    "Element " + element + " already exists");
        }
    }

    @Override
    public VersionedElement getElement(BucketName bucketName, String id) {
        ensureBucketExists(bucketName);

        PersistentBucketElement elementFromDb = getPersistentElement(bucketName, id);
        return Optional.ofNullable(elementFromDb)
                .map(it -> it.toDomainVersionedElement(bucketName))
                .orElseThrow(() -> new ElementDoesNotExistException(NamesResolver.resolve(bucketName), id));
    }

    @Override
    public VersionedElement getElement(BucketName bucketName, String id, long requiredVersion) {
        VersionedElement versionedElement = getElement(bucketName, id);
        if (versionedElement.getVersionOrThrowErrorIfEmpty() != requiredVersion) {
            throw new ConcurrentTransactionDetectedException(
                    String.format("Version of element %s changed since last read. Required version: %d", versionedElement, requiredVersion));
        }
        return versionedElement;
    }

    @Override
    public void removeElement(BucketName bucketName, String id) {
        ensureElementExists(bucketName, id);

        mongoTemplate.remove(getPersistentElement(bucketName, id), NamesResolver.resolve(bucketName));
    }

    @Override
    public boolean elementExists(BucketName bucketName, String elementId) {
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

        WriteResult updateResult = mongoTemplate.updateFirst(query, update, NamesResolver.resolve(toUpdate.getBucketName()));
        validateUpdateResultAgainstConcurrency(updateResult, toUpdate);
    }

    @Override
    public List<Element> filterElements(BucketQuery query) {
        ensureBucketExists(query.getBucketName());
        return graphQlElementsFetcher.elements(query);
    }

    @Override
    public long getNumberOfElements(BucketName bucketName) {
        ensureBucketExists(bucketName);

        return mongoTemplate.count(new Query(), PersistentBucketElement.class, NamesResolver.resolve(bucketName));
    }

    private void ensureBucketExists(BucketName bucketName) {
        if (!bucketExists(bucketName)) {
            throw new BucketDoesNotExistException(NamesResolver.resolve(bucketName));
        }
    }

    private void createShardedCollection(BucketName bucketName) {
        mongoClient.getDatabase(mongoProperties.getDatabaseName()).createCollection(NamesResolver.resolve(bucketName));
        BasicDBObject shardKey = new BasicDBObject("_id", "hashed");
        BasicDBObject shardCollection = new BasicDBObject("shardCollection", mongoProperties.getDatabaseName() + "." + bucketName);
        shardCollection.put("key", shardKey);
        mongoAdminClient.getDatabase(MONGO_ADMIN_DATABASE_NAME)
                .runCommand(shardCollection);
    }

    private void ensureElementExists(BucketName bucketName, String id) {
        getElement(bucketName, id);
    }

    private PersistentBucketElement getPersistentElement(BucketName bucketName, String id) {
        return mongoTemplate.findById(id, PersistentBucketElement.class, NamesResolver.resolve(bucketName));
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
