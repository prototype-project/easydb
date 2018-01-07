package com.easydb.easydb.config;

import com.easydb.easydb.domain.space.SpaceDefinitionRepository;
import com.easydb.easydb.infrastructure.space.MongoSpaceDefinitionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SpaceDefinitionRepositoryConfig {

    @Bean
    SpaceDefinitionRepository spaceDefinitionRepository(MongoTemplate mongoTemplate) {
        return new MongoSpaceDefinitionRepository(mongoTemplate);
    }
}
