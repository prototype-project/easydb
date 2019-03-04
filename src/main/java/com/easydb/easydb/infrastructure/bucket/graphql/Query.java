package com.easydb.easydb.infrastructure.bucket.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import java.util.List;
import java.util.Optional;

public class Query implements GraphQLQueryResolver {
    private final GraphQlMongoRepository graphQlMongoRepository;

    public final static String DEFAULT_GRAPHQL_QUERY =
            "{                  \n" +
             "    elements {    \n" +
             "        id        \n" +
             "        fields {  \n" +
             "            name  \n" +
             "            value \n" +
             "        }         \n" +
             "    }             \n" +
             "}                 \n";

    public Query(GraphQlMongoRepository graphQlMongoRepository) {
        this.graphQlMongoRepository = graphQlMongoRepository;
    }

    public List<GraphQlElement> elements(Optional<ElementFilter> filter) {
        return graphQlMongoRepository.elements(filter);
    }
}
