package com.easydb.easydb.config;

import com.easydb.easydb.domain.bucket.BucketObserversContainer;
import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.bucket.ElementService;
import com.easydb.easydb.domain.bucket.transactions.TransactionalBucketService;
import com.easydb.easydb.domain.bucket.transactions.TransactionalElementService;
import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.bucket.transactions.BucketRepository;
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager;
import com.easydb.easydb.domain.transactions.Retryer;
import com.easydb.easydb.infrastructure.bucket.MongoBucketRepository;
import com.easydb.easydb.infrastructure.bucket.graphql.GraphQlElementsFetcher;
import org.springframework.beans.factory.annotation.Qualifier;
import com.mongodb.MongoClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties(SubscribingProperties.class)
public class BucketConfig {

    @Bean
    UUIDProvider uuidProvider() {
        return new UUIDProvider();
    }

    @Bean
    BucketRepository bucketRepository(MongoTemplate mongoTemplate, @Qualifier("mongoClient") MongoClient mongoClient,
                                      @Qualifier("mongoAdminClient") MongoClient mongoAdminClient,
                                      MongoProperties mongoProperties, GraphQlElementsFetcher graphQlElementsFetcher) {
        return new MongoBucketRepository(mongoTemplate, mongoClient, mongoAdminClient, mongoProperties, graphQlElementsFetcher);
    }

    @Bean
    ElementService elementServiceFactory(BucketRepository bucketRepository) {
        return new TransactionalElementService(bucketRepository);
    }

    @Bean
    BucketService bucketServiceFactory(
            BucketRepository bucketRepository, SpaceRepository spaceRepository,
            ElementService elementService,
            OptimizedTransactionManager optimizedTransactionManager,
            BucketLocker bucketLocker,
            SpaceLocker spaceLocker,
            @Qualifier("transactionRetryer") Retryer transactionRetryer,
            @Qualifier("lockerRetryer") Retryer lockerRetryer) {
        return new TransactionalBucketService(spaceRepository, bucketRepository,
                elementService, optimizedTransactionManager, bucketLocker,
                spaceLocker, transactionRetryer, lockerRetryer);
    }

    @Bean
    BucketObserversContainer bucketObserversContainer(SubscribingProperties properties) {
        return new BucketObserversContainer(properties.getEventsObserverQueueCapacity(),
                properties.getEventsObserverThreadPoolQueueCapacity(),
                properties.getEventsObserversThreadPoolSize());
    }
}