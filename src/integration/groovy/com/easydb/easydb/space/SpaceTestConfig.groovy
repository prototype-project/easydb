package com.easydb.easydb.space

import com.easydb.easydb.domain.MainSpace
import com.easydb.easydb.domain.UUIDProvider
import com.easydb.easydb.infrastructure.bucket.MongoBucketRepository
import com.github.fakemongo.Fongo
import com.mongodb.Mongo
import com.mongodb.MongoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory

@Configuration
class SpaceTestConfig {
    private static final String SPACE_NAME = "testSpace";
    private static final String SERVER_NAME = "testServer";

    @Bean
    Mongo mongo() {
        Fongo fongo = new Fongo(SERVER_NAME)
        return fongo.getMongo()
    }

    @Primary
    @Bean
    MainSpace space(Mongo mongo) {
        MongoBucketRepository bucketRepository = new MongoBucketRepository(
                new MongoTemplate(mongo, SPACE_NAME))
        UUIDProvider uuidProvider = new UUIDProvider();

        return new MainSpace(bucketRepository, uuidProvider);
    }

    @Bean
    MongoClient mongoClient() {
        return new MongoClient("mongodb://localhost:27017/" + SPACE_NAME)
    }

    @Bean
    MongoDbFactory mongoDbFactory(Mongo mongo) {
        return new SimpleMongoDbFactory(mongo, SPACE_NAME)
    }
}
