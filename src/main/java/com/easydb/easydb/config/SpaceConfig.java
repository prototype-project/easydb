package com.easydb.easydb.config;

import com.easydb.easydb.infrastructure.space.UUIDProvider;
import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.infrastructure.space.SpaceService;
import com.easydb.easydb.infrastructure.bucket.MongoBucketRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SpaceConfig {
    @Bean
    UUIDProvider uuidProvider() {
        return new UUIDProvider();
    }

    @Bean
    SpaceService space(BucketRepository bucketRepository) {
        return new SpaceService("someName", bucketRepository);
    }

    @Bean
    MongoBucketRepository bucketRepository(MongoTemplate mongoTemplate) {
        return new MongoBucketRepository(mongoTemplate);
    }
}