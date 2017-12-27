package com.easydb.easydb.config;

import com.easydb.easydb.domain.BucketRepository;
import com.easydb.easydb.domain.MainSpace;
import com.easydb.easydb.infrastructure.MongoBucketRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SpaceConfig {
    @Bean
    MainSpace space(BucketRepository bucketRepository) {
        return new MainSpace("someSpace", bucketRepository);
    }

    @Bean
    MongoBucketRepository bucketRepository(MongoTemplate mongoTemplate) {
        return new MongoBucketRepository(mongoTemplate);
    }
}
