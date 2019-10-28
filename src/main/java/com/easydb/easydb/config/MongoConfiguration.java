package com.easydb.easydb.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Autowired
    private MongoProperties properties;

    @NotNull
    @Override
    protected String getDatabaseName() {
        return properties.getDatabaseName();
    }

    @NotNull
    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) throws Exception {
        MappingMongoConverter converter =
                new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return new MongoTemplate(mongoDbFactory, converter);
    }

    @NotNull
    @Bean
    public MongoClient mongoClient() {
        return new MongoClient(fromMongoConnectionString(properties.getHosts()),
                MongoCredential.createCredential(properties.getUsername(), getDatabaseName(), properties.getPassword().toCharArray()),
                MongoClientOptions.builder().build());
    }

    @Bean
    public MongoClient mongoAdminClient() {
        return new MongoClient(fromMongoConnectionString(properties.getHosts()),
                MongoCredential.createCredential(properties.getAdminUsername(),
                        properties.getAdminDatabaseName(), properties.getAdminPassword().toCharArray()),
                MongoClientOptions.builder().build());
    }

    private List<ServerAddress> fromMongoConnectionString(String connectionString) {
        return Arrays.stream(connectionString.split(","))
                .map(connString -> {
                    String[] split = connString.split(":");
                    return new ServerAddress(split[0].trim(), Integer.valueOf(split[1].trim()));
                }).collect(Collectors.toList());
    }
}
