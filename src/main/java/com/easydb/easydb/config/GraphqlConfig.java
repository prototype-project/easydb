package com.easydb.easydb.config;

import com.easydb.easydb.infrastructure.bucket.graphql.GraphQlProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class GraphqlConfig {

    @Bean
    public GraphQlProvider graphQlProvider(MongoTemplate mongoTemplate) {
        return new GraphQlProvider(mongoTemplate);
    }
}
