package com.easydb.easydb

import com.github.fakemongo.Fongo
import com.mongodb.Mongo
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@Configuration
class SpaceTestConfig {
    private static final String DB_NAME = "testSpace";
    private static final String SERVER_NAME = "testServer";

    @Bean
    Mongo mongo() {
        Fongo fongo = new Fongo(SERVER_NAME)
        return fongo.getMongo()
    }

    @Bean
    MongoTemplate mongoTemplate(Mongo mongo) {
        return new MongoTemplate(mongo, DB_NAME)
    }

    @Bean
    MongoClient mongoClient() {
        MongoClient mongoClient = mock(MongoClient)
        MongoDatabase mongoDatabase = mock(MongoDatabase)
        when(mongoDatabase.runCommand(any(Bson.class))).thenReturn(new Document())
        when(mongoClient.getDatabase(any(String.class))).thenReturn(mongoDatabase)
        return mongoClient
    }

    @Bean
    MongoDbFactory mongoDbFactory(Mongo mongo) {
        return new SimpleMongoDbFactory(mongo, DB_NAME)
    }
}
