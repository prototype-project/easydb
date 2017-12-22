package com.easydb.easydb.domain;

import com.easydb.easydb.infrastructure.MongoBucketRepository;
import com.github.fakemongo.Fongo;
import org.springframework.data.mongodb.core.MongoTemplate;

class SpaceConfig {
	private static final String SPACE_NAME = "testSpace";
	private static final String SERVER_NAME = "testServer";

	static MainSpace createSpace() {
		Fongo fongo = new Fongo(SERVER_NAME);
		MongoBucketRepository bucketRepository = new MongoBucketRepository(
				new MongoTemplate(fongo.getMongo(), SPACE_NAME));

		return new MainSpace(SPACE_NAME, bucketRepository);
	}

}
