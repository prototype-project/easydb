package com.easydb.easydb.infrastructure.bucket.graphql;

import com.easydb.easydb.domain.bucket.BucketObserversContainer;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.BucketSubscriptionQuery;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementEvent;
import graphql.ExecutionResult;
import java.util.List;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class GraphQlElementsFetcher {
    private final GraphQlProvider graphQlProvider;
    private final BucketObserversContainer observersContainer;

    public GraphQlElementsFetcher(GraphQlProvider graphQlProvider,
                                  BucketObserversContainer observersContainer) {
        this.graphQlProvider = graphQlProvider;
        this.observersContainer = observersContainer;
    }

    public List<Element> elements(BucketQuery query) {
        ExecutionResult executionResult = graphQlProvider.graphQL(query)
                .execute(query.getQuery().orElse(Query.DEFAULT_GRAPHQL_QUERY));

        validateResult(executionResult);
        return GraphQlToDomainObjectsConverter.of(query.getBucketName()).convertToDomainElements(executionResult.getData());
    }

    public Flux<ElementEvent> elementsEvents(BucketSubscriptionQuery query) {
        GraphQlToDomainObjectsConverter converter = GraphQlToDomainObjectsConverter.of(query.getBucketName());

        ExecutionResult executionResult = graphQlProvider.graphQL(query)
                .execute(query.getQuery().orElse(Subscription.DEFAULT_GRAPHQL_SUBSCRIPTION));

        Publisher<ExecutionResult> elementsEvents = executionResult.getData();
        validateResult(executionResult);
        return Flux.from(elementsEvents).map(result -> converter.convertToDomainElementEvent(result.getData()))
                .doFinally(it -> observersContainer.remove(query.getBucketName()));
    }

    private void validateResult(ExecutionResult executionResult) {
        if (executionResult.getErrors().size() > 0) {
            throw new QueryValidationException(String.format("Query validation error: `%s`", executionResult.getErrors().get(0).getMessage()));
        }
    }
}
