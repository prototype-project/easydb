package com.easydb.easydb;

import com.easydb.easydb.domain.Space;
import com.easydb.easydb.infrastructure.MongoBucketRepository;
import com.github.fakemongo.Fongo;
import org.springframework.data.mongodb.core.MongoTemplate;

class SpaceConfig {
	static final String SPACE_NAME = "testSpace";
	static final String SERVER_NAME = "testServer";

	static Space createSpace() {
		Fongo fongo = new Fongo(SERVER_NAME);
		MongoBucketRepository bucketRepository = new MongoBucketRepository(
				new MongoTemplate(fongo.getMongo(), SPACE_NAME));

		return new Space(SPACE_NAME, bucketRepository);
	}

}
