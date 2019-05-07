package com.easydb.easydb

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import org.bson.BsonDocument
import org.bson.Document
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.testcontainers.containers.GenericContainer

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class SpaceTestConfig {

    static def MONGO_PORT = 27017
    static def DB_NAME = "testSpace"

    @Bean(initMethod = "start", destroyMethod = "stop")
    GenericContainer mongoContainer() {
        GenericContainer genericContainer = new GenericContainer("mongo:4.0.2")
                .withExposedPorts(MONGO_PORT)
        return genericContainer
    }

    @Bean
    MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, DB_NAME)
    }

    @Bean
    MongoClient mongoClient(GenericContainer container) {
        return new MongoClient(container.getContainerIpAddress(), container.getMappedPort(MONGO_PORT))
    }

    @Bean
    MongoClient mongoAdminClient() {
        MongoClient mongoClient = mock(MongoClient)
        MongoDatabase mongoDatabase = mock(MongoDatabase)
        when(mongoDatabase.runCommand(any(BsonDocument))).thenReturn(new Document())
        when(mongoClient.getDatabase(any(String.class))).thenReturn(mongoDatabase)
        return mongoClient
    }

    @Bean
    MongoDbFactory mongoDbFactory(MongoClient mongoClient) {
        return new SimpleMongoDbFactory(mongoClient, DB_NAME)
    }
}
