package com.easydb.easydb.domain;

import com.easydb.easydb.domain.space.SpaceDefinitionRepository;
import com.easydb.easydb.infrastructure.space.MainSpace;
import com.easydb.easydb.infrastructure.bucket.MongoBucketRepository;
import com.easydb.easydb.infrastructure.space.MongoSpaceDefinitionRepository;
import com.easydb.easydb.infrastructure.space.UUIDProvider;
import com.github.fakemongo.Fongo;
import org.springframework.data.mongodb.core.MongoTemplate;

class SpaceTestConfig {
	private static final String DB_NAME = "testDb";
	private static final String SERVER_NAME = "testServer";
	private static final Fongo FONGO = new Fongo(SERVER_NAME);


	static MainSpace createSpace() {
		MongoBucketRepository bucketRepository = new MongoBucketRepository(
				new MongoTemplate(FONGO.getMongo(), DB_NAME));

		UUIDProvider uuidProvider = new UUIDProvider();

		return new MainSpace("spaceName", bucketRepository, uuidProvider);
	}

	static SpaceDefinitionRepository createSpaceRepository() {
		return new MongoSpaceDefinitionRepository(new MongoTemplate(FONGO.getMongo(), DB_NAME));
	}
}