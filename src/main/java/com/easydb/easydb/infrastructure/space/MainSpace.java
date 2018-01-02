package com.easydb.easydb.infrastructure.space;

import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.bucket.*;
import com.easydb.easydb.domain.bucket.dto.ElementCreateDto;
import com.easydb.easydb.domain.bucket.dto.ElementQueryDto;
import com.easydb.easydb.domain.bucket.dto.ElementUpdateDto;

import java.util.List;
import java.util.stream.Collectors;

public class MainSpace implements Space {
	private final String name;
	private final BucketRepository bucketRepository;
	private final UUIDProvider uuidProvider;

	public MainSpace(
			String name,
			BucketRepository bucketRepository,
			UUIDProvider uuidProvider) {
		this.name = name;
		this.bucketRepository = bucketRepository;
		this.uuidProvider = uuidProvider;
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
		Element created = bucketRepository.insertElement(Element.of(uuidProvider.generateUUID(), element));
		return created.toQueryDto();
	}

	@Override
	public ElementQueryDto getElement(String bucketName, String id) {
		return bucketRepository.getElement(bucketName, id).toQueryDto();
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
	public void updateElement(ElementUpdateDto toUpdate) {
		bucketRepository.updateElement(Element.of(toUpdate));
	}

	@Override
	public List<ElementQueryDto> getAllElements(String name) {
		return bucketRepository.getAllElements(name).stream()
				.map(Element::toQueryDto)
				.collect(Collectors.toList());
	}
}
