package com.easydb.easydb.domain.bucket;

import java.util.List;

public interface BucketRepository {

    boolean bucketExists(String name);

    void removeBucket(String name) throws BucketDoesNotExistException;

    void insertElement(Element element);

    Element getElement(String bucketName, String id) throws BucketDoesNotExistException, ElementDoesNotExistException;

    void removeElement(String bucketName, String id) throws BucketDoesNotExistException, ElementDoesNotExistException;

    boolean elementExists(String bucketName, String elementId) throws BucketDoesNotExistException;

    void updateElement(Element toUpdate) throws BucketDoesNotExistException, ElementDoesNotExistException;

    List<Element> filterElements(BucketQuery query);

    long getNumberOfElements(String bucketName) throws BucketDoesNotExistException;
}
