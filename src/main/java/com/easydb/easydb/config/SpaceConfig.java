package com.easydb.easydb.config;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.MainSpace;
import com.easydb.easydb.infrastructure.MongoBucketRepository;
import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SpaceConfig {
    @Bean
    MainSpace space(BucketRepository bucketRepository) {
        return new MainSpace(bucketRepository);
    }

    @Bean
    MongoBucketRepository bucketRepository(Mongo mongo, @Value("${space.name}") String spaceName) {
        return new MongoBucketRepository(new MongoTemplate(mongo, spaceName));
    }
}