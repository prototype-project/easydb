package com.easydb.easydb.config;

import com.google.common.collect.Lists;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Autowired
    private MongoProperties properties;

    @Override
    protected String getDatabaseName() {
        return properties.getDatabaseName();
    }

    @Bean
    public Mongo mongo() throws Exception {
        return mongoClient();
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), getDatabaseName());
    }

    @Bean
    MongoClient mongoClient() {
        return new MongoClient(new ServerAddress(properties.getHost(), properties.getPort()),
                Lists.newArrayList(MongoCredential.createCredential(properties.getUsername(), getDatabaseName(), properties.getPassword().toCharArray())));
    }

    @Bean
    MongoClient mongoAdminClient() {
        return new MongoClient(new ServerAddress(properties.getHost(), properties.getPort()),
                Lists.newArrayList(MongoCredential.createCredential(properties.getAdminUsername(),
                        properties.getAdminDatabaseName(), properties.getAdminPassword().toCharArray())));
    }
}
