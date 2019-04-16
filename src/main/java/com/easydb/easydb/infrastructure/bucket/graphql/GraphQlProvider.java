package com.easydb.easydb.infrastructure.bucket.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.coxautodev.graphql.tools.SchemaParser;
import com.easydb.easydb.domain.bucket.BucketEventsObserver;
import com.easydb.easydb.domain.bucket.BucketObserversContainer;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.BucketSubscriptionQuery;
import graphql.GraphQL;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.schema.GraphQLSchema;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;

public class GraphQlProvider {
    private static String FILTERING_SCHEMA_FILE_NAME = "/filteringSchema.graphql";
    private static String SUBSCRIPTION_SCHEMA_FILE_NAME = "/subscriptionSchema.graphql";

    private final String filteringSchema;
    private final String subscriptionSchema;

    private final MongoTemplate mongoTemplate;
    private final ElementFilterToMongoQueryConverter mongoQueryConverter;
    private final BucketObserversContainer observersContainer;

    public GraphQlProvider(MongoTemplate mongoTemplate,
                           ElementFilterToMongoQueryConverter mongoQueryConverter,
                           BucketObserversContainer observersContainer) {
        this.mongoQueryConverter = mongoQueryConverter;
        this.mongoTemplate = mongoTemplate;
        this.observersContainer = observersContainer;

        this.filteringSchema = readSchema(FILTERING_SCHEMA_FILE_NAME);
        this.subscriptionSchema = readSchema(SUBSCRIPTION_SCHEMA_FILE_NAME);
    }

    GraphQL graphQL(BucketQuery bucketQuery) {
        GraphQLSchema graphQLSchema = SchemaParser.newParser().schemaString(filteringSchema)
                .resolvers(new Query(new GraphQlMongoRepository(mongoQueryConverter, bucketQuery, mongoTemplate)))
                .build()
                .makeExecutableSchema();
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    GraphQL graphQL(BucketSubscriptionQuery subscriptionQuery) {
        BucketEventsObserver observer = observersContainer.provide(subscriptionQuery.getBucketName());

        GraphQLSchema graphQLSchema = SchemaParser.newParser().schemaString(subscriptionSchema)
                .resolvers(new GraphQLQueryResolver() {} ,new Subscription(observer))
                .build()
                .makeExecutableSchema();

        return GraphQL.newGraphQL(graphQLSchema)
                .subscriptionExecutionStrategy(new SubscriptionExecutionStrategy())
                .build();
    }

    private String readSchema(String fileName) {
        try {
            InputStream in = getClass().getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return reader.lines().collect(Collectors.joining());
        } catch (Exception ex) {
            throw new RuntimeException("Error during reading schema " + fileName, ex);
        }
    }
}
