package com.easydb.easydb.domain;

import java.util.List;

public class Space {
	private final String name;
	private final BucketRepository bucketRepository;

	public Space(String name, BucketRepository bucketRepository) {
		this.name = name;
		this.bucketRepository = bucketRepository;
	}

	public void createBucket(String name, List<String> fieldsNames) {
		bucketRepository.create(BucketDefinition.of(name, fieldsNames));
	}

	public boolean bucketExists(String name) {
		return true;
	}

	public void removeBucket(String name) {
	}

	public ElementQueryDto addElement(ElementCreateDto p) {
		return null;
	}

	public ElementQueryDto getElement(String bucketName, String id) {
		return null;
	}

	public void removeElement(String bucketName, String elementId) {
	}

	public boolean elementExists(String bucketName, String elementId) {
		return true;
	}

	public void updateElement(ElementUpdateDto toUpdate) {
	}
}
