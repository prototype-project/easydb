package com.easydb.easydb.domain

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.space.SpaceDefinitionRepository;
import com.easydb.easydb.domain.space.SpaceFactory;
import com.easydb.easydb.infrastructure.bucket.MongoBucketRepository
import com.easydb.easydb.infrastructure.space.MainSpaceFactory;
import com.easydb.easydb.infrastructure.space.MongoSpaceDefinitionRepository;
import com.github.fakemongo.Fongo;
import org.springframework.data.mongodb.core.MongoTemplate;

class SpaceTestConfig {
	static String DB_NAME = "testDb"
	static String SERVER_NAME = "testServer"
	static Fongo FONGO = new Fongo(SERVER_NAME)
	static BucketRepository BUCKET_REPOSITORY = new MongoBucketRepository(
			new MongoTemplate(FONGO.getMongo(), DB_NAME))
	static SpaceFactory SPACE_FACTORY = new MainSpaceFactory(BUCKET_REPOSITORY)
	static SpaceDefinitionRepository SPACE_DEFINITION_REPOSITORY = new MongoSpaceDefinitionRepository(
			new MongoTemplate(FONGO.getMongo(), DB_NAME))
}