package com.easydb.easydb.config;

import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.infrastructure.space.MongoSpaceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SpaceDefinitionRepositoryConfig {

    @Bean
    SpaceRepository spaceDefinitionRepository(MongoTemplate mongoTemplate) {
        return new MongoSpaceRepository(mongoTemplate);
    }
}
