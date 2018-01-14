package com.easydb.easydb.space

import com.github.fakemongo.Fongo
import com.mongodb.Mongo
import com.mongodb.MongoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory

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
        return new MongoClient("mongodb://localhost:27017/" + DB_NAME)
    }

    @Bean
    MongoDbFactory mongoDbFactory(Mongo mongo) {
        return new SimpleMongoDbFactory(mongo, DB_NAME)
    }
}
