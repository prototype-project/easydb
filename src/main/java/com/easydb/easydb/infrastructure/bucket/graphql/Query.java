package com.easydb.easydb.infrastructure.bucket.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import java.util.List;
import java.util.Optional;

public class Query implements GraphQLQueryResolver {
    private final GraphQlMongoRepository graphQlMongoRepository;

    public Query(GraphQlMongoRepository graphQlMongoRepository) {
        this.graphQlMongoRepository = graphQlMongoRepository;
    }

    public List<GraphQlElement> elements(Optional<ElementFilter> filter) {
        return graphQlMongoRepository.elements(filter);
    }
}
