package com.easydb.easydb.domain.space

import com.easydb.easydb.domain.bucket.BucketRepository
import com.easydb.easydb.infrastructure.bucket.MongoBucketRepository
import com.easydb.easydb.infrastructure.space.MongoSpaceRepository
import com.github.fakemongo.Fongo
import org.springframework.data.mongodb.core.MongoTemplate
import spock.lang.Shared
import spock.lang.Specification

class BaseSpecification extends Specification{
    @Shared
    String DB_NAME = "testDb"

    @Shared
    String SERVER_NAME = "testServer"

    @Shared
    Fongo fongo = new Fongo(SERVER_NAME)

    @Shared
    BucketRepository bucketRepository = new MongoBucketRepository(new MongoTemplate(fongo.getMongo(), DB_NAME))

    @Shared
    SpaceServiceFactory spaceFactory = new SpaceServiceFactory(bucketRepository)

    @Shared
    SpaceRepository spaceRepository = new MongoSpaceRepository(new MongoTemplate(fongo.getMongo(), DB_NAME))
}
