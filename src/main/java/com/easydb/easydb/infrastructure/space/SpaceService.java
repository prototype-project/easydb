package com.easydb.easydb.infrastructure.space;

import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.bucket.*;

import java.util.List;
import java.util.stream.Collectors;

public class SpaceService implements Space {
	private final String spaceName;
	private final BucketRepository bucketRepository;

	public SpaceService(
			String spaceName,
			BucketRepository bucketRepository) {
		this.spaceName = spaceName;
		this.bucketRepository = bucketRepository;
	}

	@Override
	public boolean bucketExists(String bucketName) {
		return bucketRepository.exists(getBucketName(bucketName));
	}

	@Override
	public void removeBucket(String bucketName) {
		bucketRepository.remove(getBucketName(bucketName));
	}

	@Override
	public void addElement(Element element) {
		bucketRepository.insertElement(
				Element.of(element.getId(), getBucketName(element.getBucketName()), element.getFields()));
	}

	@Override
	public Element getElement(String bucketName, String id) {
		Element element = bucketRepository.getElement(getBucketName(bucketName), id);
		return Element.of(element.getId(), bucketName, element.getFields());
	}

	@Override
	public void removeElement(String bucketName, String elementId) {
		bucketRepository.removeElement(getBucketName(bucketName), elementId);
	}

	@Override
	public boolean elementExists(String bucketName, String elementId) {
		return bucketRepository.elementExists(getBucketName(bucketName), elementId);
	}

	@Override
	public void updateElement(Element toUpdate) {
		bucketRepository.updateElement(Element.of(toUpdate.getId(), getBucketName(toUpdate.getBucketName()), toUpdate.getFields()));
	}

	@Override
	public List<Element> getAllElements(String bucketName) {
		return bucketRepository.getAllElements(getBucketName(bucketName)).stream()
				.map(it -> Element.of(it.getId(), bucketName, it.getFields()))
				.collect(Collectors.toList());
	}

	private String getBucketName(String bucketName) {
		return spaceName + ":" + bucketName;
	}
}