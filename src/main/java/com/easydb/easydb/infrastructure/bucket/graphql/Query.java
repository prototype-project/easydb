package com.easydb.easydb.infrastructure.bucket.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import java.util.List;
import java.util.Optional;

public class Query implements GraphQLQueryResolver {
    private final MongoRepository mongoRepository;

    public Query(MongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    public List<Element> elements(Optional<ElementFilter> filter) {
        return mongoRepository.elements(filter);
    }
}
