package com.easydb.easydb.config;

import com.easydb.easydb.domain.bucket.BucketObserversContainer;
import com.easydb.easydb.infrastructure.bucket.graphql.ElementFilterToMongoQueryConverter;
import com.easydb.easydb.infrastructure.bucket.graphql.GraphQlElementsFetcher;
import com.easydb.easydb.infrastructure.bucket.graphql.GraphQlEventsPublisher;
import com.easydb.easydb.infrastructure.bucket.graphql.GraphQlProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
    import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class GraphqlConfig {

    @Bean
    public ElementFilterToMongoQueryConverter converter() {
        return new ElementFilterToMongoQueryConverter();
    }

    @Bean
    public GraphQlElementsFetcher graphQlElementsFetcher(GraphQlProvider graphQlProvider) {
        return new GraphQlElementsFetcher(graphQlProvider);
    }

    @Bean
    public GraphQlEventsPublisher graphQlEventsPublisher(GraphQlProvider graphQlProvider) {
        return new GraphQlEventsPublisher(graphQlProvider);
    }

    @Bean
    public GraphQlProvider graphQlProvider(MongoTemplate mongoTemplate,
                                           ElementFilterToMongoQueryConverter elementFilterToMongoQueryConverter,
                                           BucketObserversContainer observersContainer) {
        return new GraphQlProvider(mongoTemplate, elementFilterToMongoQueryConverter, observersContainer);
    }
}
