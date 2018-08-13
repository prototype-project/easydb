package com.easydb.easydb.domain.bucket;

import java.util.List;

public interface BucketRepository {

    boolean bucketExists(String name);

    void removeBucket(String name) throws BucketDoesNotExistException;

    void insertElement(Element element);

    VersionedElement getElement(String bucketName, String id)
            throws BucketDoesNotExistException, ElementDoesNotExistException;

    VersionedElement getElement(String bucketName, String id, long requiredVersion)
            throws BucketDoesNotExistException, ElementDoesNotExistException;

    void removeElement(String bucketName, String id) throws BucketDoesNotExistException, ElementDoesNotExistException;

    boolean elementExists(String bucketName, String elementId) throws BucketDoesNotExistException;

    void updateElement(VersionedElement toUpdate) throws BucketDoesNotExistException, ElementDoesNotExistException;

    List<VersionedElement> filterElements(BucketQuery query);

    long getNumberOfElements(String bucketName) throws BucketDoesNotExistException;
}
