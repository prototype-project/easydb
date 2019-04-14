package com.easydb.easydb.infrastructure.bucket.graphql;

import  com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.NamesResolver;
import com.easydb.easydb.infrastructure.bucket.PersistentBucketElement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public class GraphQlMongoRepository {
    private final ElementFilterToMongoQueryConverter transformer;
    private final BucketQuery bucketQuery;
    private final MongoTemplate mongoTemplate;

    GraphQlMongoRepository(ElementFilterToMongoQueryConverter transformer, BucketQuery bucketQuery,
                           MongoTemplate mongoTemplate) {
        this.transformer = transformer;
        this.bucketQuery = bucketQuery;
        this.mongoTemplate = mongoTemplate;
    }

    public List<GraphQlElement> elements(Optional<ElementFilter> filter) {
        Query mongoQuery = transformer.transform(filter);
        mongoQuery.limit(bucketQuery.getLimit());
        mongoQuery.skip(bucketQuery.getOffset());
        return mongoTemplate.find(mongoQuery, PersistentBucketElement.class, NamesResolver.resolve(bucketQuery.getBucketName())).stream()
                .map(PersistentBucketElement::toGraphQlElement)
                .collect(Collectors.toList());
    }
}
