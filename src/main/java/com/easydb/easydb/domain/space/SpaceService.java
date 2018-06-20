package com.easydb.easydb.domain.space;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.BucketService;

public class SpaceService {

	private final SpaceRepository spaceRepository;
	private final BucketService bucketService;

	public SpaceService(SpaceRepository spaceRepository, BucketService bucketService) {
		this.spaceRepository = spaceRepository;
		this.bucketService = bucketService;
	}

	void remove(String name) {
//		bucketService.removeBucketsForSpace();
	}
}
