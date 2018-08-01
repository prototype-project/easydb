package com.easydb.easydb.config;

import com.easydb.easydb.domain.bucket.BucketServiceFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.transactions.TransactionManagerFactory;
import com.easydb.easydb.infrastructure.bucket.MongoBucketRepository;
import com.easydb.easydb.infrastructure.bucket.TransactionalBucketServiceFactory;
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
    BucketServiceFactory bucketServiceFactory(
            BucketRepository bucketRepository, SpaceRepository spaceRepository,
            TransactionManagerFactory transactionManagerFactory) {
        return new TransactionalBucketServiceFactory(bucketRepository, spaceRepository, transactionManagerFactory);
    }

    @Bean
    BucketRepository bucketRepository(MongoTemplate mongoTemplate) {
        return new MongoBucketRepository(mongoTemplate);
    }
}