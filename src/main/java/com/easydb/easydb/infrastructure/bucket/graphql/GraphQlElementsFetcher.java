package com.easydb.easydb.infrastructure.bucket.graphql;

import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.Element;
import graphql.ExecutionResult;
import java.util.List;

public class GraphQlElementsFetcher {
    private final GraphQlProvider graphQlProvider;

    public GraphQlElementsFetcher(GraphQlProvider graphQlProvider) {
        this.graphQlProvider = graphQlProvider;
    }

    public List<Element> elements(BucketQuery query) {
        ExecutionResult executionResult = graphQlProvider.graphQL(query)
                .execute(query.getQuery().orElse(Query.DEFAULT_GRAPHQL_QUERY));

        if (executionResult.getErrors().size() > 0) {
            throw new QueryValidationException(String.format("Query validation error: `%s`", executionResult.getErrors().get(0).getMessage()));
        }
        return GraphQlToDomainElementConverter.of(query.getBucketName()).convertToDomainElements(executionResult.getData());
    }
}
