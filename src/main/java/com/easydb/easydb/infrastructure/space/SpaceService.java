package com.easydb.easydb.infrastructure.space;

import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.bucket.*;

import java.util.List;

public class SpaceService implements Space {
	private final String name;
	private final BucketRepository bucketRepository;

	public SpaceService(
			String name,
			BucketRepository bucketRepository) {
		this.name = name;
		this.bucketRepository = bucketRepository;
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
	public void addElement(Element element) {
		bucketRepository.insertElement(element);
	}

	@Override
	public Element getElement(String bucketName, String id) {
		return bucketRepository.getElement(bucketName, id);
	}

	@Override
	public void removeElement(String bucketName, String elementId) {
		bucketRepository.removeElement(bucketName, elementId);
	}

	@Override
	public boolean elementExists(String bucketName, String elementId) {
		return bucketRepository.elementExists(bucketName, elementId);
	}

	@Override
	public void updateElement(Element toUpdate) {
		bucketRepository.updateElement(toUpdate);
	}

	@Override
	public List<Element> getAllElements(String name) {
		return bucketRepository.getAllElements(name);
	}
}
