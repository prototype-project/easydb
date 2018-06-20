package com.easydb.easydb.domain.space;

import com.easydb.easydb.domain.bucket.BucketService;

public class SpaceService {

	private final BucketServiceFactory bucketServiceFactory;
	private final SpaceRepository spaceRepository;

	public SpaceService(BucketServiceFactory bucketServiceFactory, SpaceRepository spaceRepository) {
		this.bucketServiceFactory = bucketServiceFactory;
		this.spaceRepository = spaceRepository;
	}

	public void save(Space toSave) {
		spaceRepository.save(toSave);
	}

	public boolean exists(String name) {
		return spaceRepository.exists(name);
	}

	public Space get(String name) {
		return spaceRepository.get(name);
	}

	public void remove(String name) {
		Space space = spaceRepository.get(name);
		BucketService bucketService = bucketServiceFactory.buildBucketService(name);
		space.getBuckets().forEach(bucketService::removeBucket);
		spaceRepository.remove(name);
	}

	public void update(Space toUpdate) {
		spaceRepository.update(toUpdate);
	}

	public BucketService bucketServiceForSpace(String spaceName) {
		return bucketServiceFactory.buildBucketService(spaceName);
	}
}
