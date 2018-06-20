package com.easydb.easydb.config;

import com.easydb.easydb.domain.space.BucketServiceFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
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
    BucketServiceFactory spaceFactory(BucketRepository bucketRepository, SpaceRepository spaceRepository) {
        return new BucketServiceFactory(bucketRepository, spaceRepository);
    }

    @Bean
    BucketRepository bucketRepository(MongoTemplate mongoTemplate) {
        return new MongoBucketRepository(mongoTemplate);
    }
}