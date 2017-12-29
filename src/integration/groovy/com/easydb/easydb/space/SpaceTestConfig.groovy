package com.easydb.easydb.space

import com.easydb.easydb.domain.MainSpace
import com.easydb.easydb.domain.UUIDProvider
import com.easydb.easydb.infrastructure.bucket.MongoBucketRepository
import com.github.fakemongo.Fongo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.core.MongoTemplate

class SpaceTestConfig {
    private static final String SPACE_NAME = "testSpace";
    private static final String SERVER_NAME = "testServer";

    @Bean
    @Primary
    static MainSpace space() {
        Fongo fongo = new Fongo(SERVER_NAME);
        MongoBucketRepository bucketRepository = new MongoBucketRepository(
                new MongoTemplate(fongo.getMongo(), SPACE_NAME));
        UUIDProvider uuidProvider = new UUIDProvider();

        return new MainSpace(bucketRepository, uuidProvider);
    }
}
