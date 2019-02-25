package com.easydb.easydb.config;

import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory;
import com.easydb.easydb.domain.bucket.factories.ElementServiceFactory;
import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager;
import com.easydb.easydb.domain.transactions.Retryier;
import com.easydb.easydb.infrastructure.bucket.MongoBucketRepository;
import com.easydb.easydb.domain.bucket.factories.TransactionalBucketServiceFactory;
import com.easydb.easydb.infrastructure.bucket.graphql.GraphQlProvider;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Qualifier;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class BucketConfig {

    @Bean
    UUIDProvider uuidProvider() {
        return new UUIDProvider();
    }

    @Bean
    BucketRepository bucketRepository(MongoTemplate mongoTemplate, @Qualifier("mongoClient") MongoClient mongoClient,
                                      @Qualifier("mongoAdminClient") MongoClient mongoAdminClient,
                                      MongoProperties mongoProperties, GraphQlProvider graphQlProvider) {
        return new MongoBucketRepository(mongoTemplate, mongoClient, mongoAdminClient, mongoProperties, graphQlProvider);
    }

    @Bean
    ElementServiceFactory elementServiceFactory(BucketRepository bucketRepository) {
        return new ElementServiceFactory(bucketRepository);
    }

    @Bean
    BucketServiceFactory bucketServiceFactory(
            BucketRepository bucketRepository, SpaceRepository spaceRepository,
            ElementServiceFactory elementServiceFactory,
            OptimizedTransactionManager optimizedTransactionManager,
            BucketLocker bucketLocker,
            SpaceLocker spaceLocker,
            @Qualifier("transactionRetryier")  Retryier transactionRetryier,
            @Qualifier("lockerRetryier") Retryier lockerRetryier) {
        return new TransactionalBucketServiceFactory(spaceRepository, bucketRepository,
                elementServiceFactory, optimizedTransactionManager, bucketLocker,
                spaceLocker, transactionRetryier, lockerRetryier);
    }
}