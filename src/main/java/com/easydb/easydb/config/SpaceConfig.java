package com.easydb.easydb.config;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.SpaceService;
import com.easydb.easydb.domain.transactions.Retryier;
import com.easydb.easydb.infrastructure.space.MongoSpaceRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SpaceConfig {

    @Bean
    SpaceRepository spaceDefinitionRepository(MongoTemplate mongoTemplate) {
        return new MongoSpaceRepository(mongoTemplate);
    }

    @Bean
    SpaceService spaceService(SpaceRepository spaceRepository, BucketRepository bucketRepository,
                              SpaceLocker spaceLocker, BucketLocker bucketLocker,
                              @Qualifier("lockerRetryier") Retryier lockerRetryier) {
        return new SpaceService(spaceRepository, bucketRepository, spaceLocker, bucketLocker, lockerRetryier);
    }
}