package com.easydb.easydb.config;

import com.google.common.collect.Lists;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
        return new MongoClient(fromMongoConnectionString(properties.getHosts()), Lists.newArrayList(
                MongoCredential.createCredential(properties.getUsername(), getDatabaseName(), properties.getPassword().toCharArray())));
    }

    @Bean
    MongoClient mongoAdminClient() {
        return new MongoClient(fromMongoConnectionString(properties.getHosts()),
                Lists.newArrayList(MongoCredential.createCredential(properties.getAdminUsername(),
                        properties.getAdminDatabaseName(), properties.getAdminPassword().toCharArray())));
    }

    private List<ServerAddress> fromMongoConnectionString(String connectionString) {
        return Arrays.stream(connectionString.split(","))
                .map(connString -> {
                    String[] split = connString.split(":");
                    return new ServerAddress(split[0].trim(), Integer.valueOf(split[1].trim()));
                }).collect(Collectors.toList());
    }
}
