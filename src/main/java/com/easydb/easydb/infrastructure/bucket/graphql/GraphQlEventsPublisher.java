package com.easydb.easydb.infrastructure.bucket.graphql;

import com.easydb.easydb.domain.bucket.BucketEventsPublisher;
import com.easydb.easydb.domain.bucket.BucketSubscriptionQuery;
import com.easydb.easydb.domain.bucket.ElementEvent;
import reactor.core.publisher.Flux;

public class GraphQlEventsPublisher implements BucketEventsPublisher {

    private final GraphQlProvider graphQlProvider;

    public GraphQlEventsPublisher(GraphQlProvider graphQlProvider) {
        this.graphQlProvider = graphQlProvider;
    }

    @Override
    public Flux<ElementEvent> subscribe(BucketSubscriptionQuery query) {
        return graphQlProvider.graphQL(query)
                .execute(query.getQuery().orElse(Subscription.DEFAULT_GRAPHQL_SUBSCRIPTION)).getData();
    }
}
