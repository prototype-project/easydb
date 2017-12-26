package com.easydb.easydb.domain;

import java.util.List;
import java.util.stream.Collectors;

public class MainSpace implements Space {
	private final String name;
	private final BucketRepository bucketRepository;

	public MainSpace(String name, BucketRepository bucketRepository) {
		this.name = name;
		this.bucketRepository = bucketRepository;
	}

	@Override
	public void createBucket(String name) {
		bucketRepository.create(BucketDefinition.of(name));
	}

	@Override
	public boolean bucketExists(String name) {
		return bucketRepository.exists(name);
	}

	@Override
	public void removeBucket(String name) {
		bucketRepository.remove(name);
	}

	@Override
	public ElementQueryDto addElement(ElementCreateDto element) {
		BucketElement created = bucketRepository.insertElement(BucketElement.of(element));
		return created.toQueryDto();
	}

	@Override
	public ElementQueryDto getElement(String bucketName, String id) {
		if (!bucketExists(bucketName)) {
			throw new BucketDoesNotExistException(bucketName);
		}
		return bucketRepository.getElement(bucketName, id).toQueryDto();
	}

	@Override
	public void removeElement(String bucketName, String elementId) {
		try {
			bucketRepository.removeElement(bucketName, elementId);
		}
		catch (BucketElementDoesNotExistException e) {
			throw new ElementDoesNotExistException(bucketName, elementId);
		}
	}

	@Override
	public boolean elementExists(String bucketName, String elementId) {
		return bucketRepository.elementExists(bucketName, elementId);
	}

	@Override
	public void updateElement(ElementUpdateDto toUpdate) {
		if (!bucketExists(toUpdate.getBucketName())) {
			throw new BucketDoesNotExistException(toUpdate.getBucketName());
		}
		bucketRepository.updateElement(BucketElement.of(toUpdate));
	}

	@Override
	public List<ElementQueryDto> getAllElements(String name) {
		return bucketRepository.getAllElements(name).stream()
				.map(BucketElement::toQueryDto)
				.collect(Collectors.toList());
	}
}
