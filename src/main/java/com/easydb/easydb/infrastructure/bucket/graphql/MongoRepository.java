package com.easydb.easydb.infrastructure.bucket.graphql;

import com.easydb.easydb.domain.bucket.BucketQuery;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public class MongoRepository {
    private final ElementFilterToMongoQueryTransformer transformer;
    private final BucketQuery bucketQuery;
    private final MongoTemplate mongoTemplate;

    MongoRepository(ElementFilterToMongoQueryTransformer transformer, BucketQuery bucketQuery,
                    MongoTemplate mongoTemplate) {
        this.transformer = transformer;
        this.bucketQuery = bucketQuery;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Element> elements(Optional<ElementFilter> filter) {
        Query mongoQuery = transformer.transform(filter);
        return null;
    }
}
