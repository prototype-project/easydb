package com.easydb.easydb.domain.bucket;

import com.easydb.easydb.domain.space.Space;
import java.util.List;
import java.util.stream.Collectors;


public class BucketService {
	private final Space space;
	private final BucketRepository bucketRepository;

	public BucketService(
			Space spaceName,
			BucketRepository bucketRepository) {
		this.space = spaceName;
		this.bucketRepository = bucketRepository;
	}

	public boolean bucketExists(String bucketName) {
		return bucketRepository.bucketExists(getBucketName(bucketName));
	}

	public void removeBucket(String bucketName) {
		bucketRepository.removeBucket(getBucketName(bucketName));
	}

	public void addElement(Element element) {
		bucketRepository.insertElement(
				Element.of(element.getId(), getBucketName(element.getBucketName()), element.getFields()));
	}

	public Element getElement(String bucketName, String id) {
		Element element = bucketRepository.getElement(getBucketName(bucketName), id);
		return Element.of(element.getId(), bucketName, element.getFields());
	}

	public void removeElement(String bucketName, String elementId) {
		bucketRepository.removeElement(getBucketName(bucketName), elementId);
	}

	public boolean elementExists(String bucketName, String elementId) {
		return bucketRepository.elementExists(getBucketName(bucketName), elementId);
	}

	public void updateElement(Element toUpdate) {
		bucketRepository.updateElement(
				Element.of(toUpdate.getId(), getBucketName(toUpdate.getBucketName()), toUpdate.getFields()));
	}

	public List<Element> filterElements(BucketQuery query) {
		return bucketRepository.filterElements(rebuildToProperSpaceName(query)).stream()
				.map(it -> Element.of(it.getId(), query.getBucketName(), it.getFields()))
				.collect(Collectors.toList());
	}

	public long getNumberOfElements(String bucketName) {
		return bucketRepository.getNumberOfElements(getBucketName(bucketName));
	}

	private BucketQuery rebuildToProperSpaceName(BucketQuery query) {
		return query.rename(getBucketName(query.getBucketName()));
	}

	private String getBucketName(String bucketName) {
		return space.getName() + ":" + bucketName;
	}
}