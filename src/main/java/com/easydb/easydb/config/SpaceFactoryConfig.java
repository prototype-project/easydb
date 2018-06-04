package com.easydb.easydb.config;

import com.easydb.easydb.domain.space.SpaceServiceFactory;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.infrastructure.bucket.MongoBucketRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SpaceFactoryConfig {
    @Bean
    UUIDProvider uuidProvider() {
        return new UUIDProvider();
    }

    @Bean
    SpaceServiceFactory spaceFactory(BucketRepository bucketRepository) {
        return new SpaceServiceFactory(bucketRepository);
    }

    @Bean
    BucketRepository bucketRepository(MongoTemplate mongoTemplate) {
        return new MongoBucketRepository(mongoTemplate);
    }
}