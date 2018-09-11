package com.easydb.easydb.config;

import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory;
import com.easydb.easydb.domain.bucket.factories.SimpleElementOperationsFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager;
import com.easydb.easydb.domain.transactions.TransactionRetryier;
import com.easydb.easydb.infrastructure.bucket.MongoBucketRepository;
import com.easydb.easydb.domain.bucket.factories.TransactionalBucketServiceFactory;
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
    BucketRepository bucketRepository(MongoTemplate mongoTemplate) {
        return new MongoBucketRepository(mongoTemplate);
    }

    @Bean
    SimpleElementOperationsFactory simpleElementOperationsFactory(SpaceRepository spaceRepository,
                                                                  BucketRepository bucketRepository) {
        return new SimpleElementOperationsFactory(spaceRepository, bucketRepository);
    }

    @Bean
    BucketServiceFactory bucketServiceFactory(
            BucketRepository bucketRepository, SpaceRepository spaceRepository,
            SimpleElementOperationsFactory simpleElementOperationsFactory,
            OptimizedTransactionManager optimizedTransactionManager,
            TransactionRetryier transactionRetryier) {
        return new TransactionalBucketServiceFactory(spaceRepository, bucketRepository,
                simpleElementOperationsFactory, optimizedTransactionManager, transactionRetryier);
    }
}