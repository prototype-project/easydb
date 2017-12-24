package com.easydb.easydb;

import com.easydb.easydb.domain.BucketDoesNotExistException
import com.easydb.easydb.domain.BucketExistsException
import com.easydb.easydb.domain.ElementCreateDto
import com.easydb.easydb.domain.ElementQueryDto
import com.easydb.easydb.domain.ElementUpdateDto
import com.easydb.easydb.domain.Space;

class InMemorySpace implements Space {
    Map<String, Map<String, ElementQueryDto>> elements = [:]

    @Override
    void createBucket(String name, List<String> fields) {
        if (bucketExists(name))
            throw new BucketExistsException("Bucket already exists")
        elements.put(name, new HashMap<String, ElementQueryDto>())
    }

    @Override
    boolean bucketExists(String name) {
        return elements.containsKey(name)
    }

    @Override
    void removeBucket(String name) {
        if (!bucketExists(name))
            throw new BucketDoesNotExistException("Bucket does not exist")
        elements.remove(name)
    }

    @Override
    ElementQueryDto getElement(String bucketName, String id) {
        return elements.get(bucketName).get(id)
    }

    @Override
    void removeElement(String bucketName, String elementId) {
        elements.get(bucketName).remove(elementId)
    }

    @Override
    boolean elementExists(String bucketName, String elementId) {
        return false
    }

    @Override
    void updateElement(ElementUpdateDto toUpdate) {
        elements.get(toUpdate.getBucketName()).remove(toUpdate.getElementId())
        ElementQueryDto updatedElement = ElementQueryDto.of(toUpdate.getElementId(), toUpdate.getBucketName(), toUpdate.getFields())
        elements.get(toUpdate.getBucketName()).put(toUpdate.getElementId(), updatedElement)
    }

    @Override
    List<ElementQueryDto> getAllElements(String name) {
        return null
    }

    @Override
    ElementQueryDto addElement(ElementCreateDto toCreate) {
        String id = UUID.randomUUID().toString()
        ElementQueryDto savedElement = ElementQueryDto.of(id, toCreate.getBucketName(), toCreate.getFields())
        elements.get(toCreate.getBucketName()).put(id, savedElement)
        return savedElement
    }
}
