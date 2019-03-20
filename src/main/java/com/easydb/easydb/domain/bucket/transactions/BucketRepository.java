package com.easydb.easydb.domain.bucket.transactions;

import com.easydb.easydb.domain.bucket.BucketDoesNotExistException;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementAlreadyExistsException;
import com.easydb.easydb.domain.bucket.ElementDoesNotExistException;
import com.easydb.easydb.domain.transactions.ConcurrentTransactionDetectedException;
import java.util.List;

public interface BucketRepository {

    boolean bucketExists(String name);

    void createBucket(String name);

    void removeBucket(String name) throws BucketDoesNotExistException;

    void insertElement(Element element) throws ElementAlreadyExistsException;

    VersionedElement getElement(String bucketName, String id)
            throws BucketDoesNotExistException, ElementDoesNotExistException;

    VersionedElement getElement(String bucketName, String id, long requiredVersion)
            throws BucketDoesNotExistException, ElementDoesNotExistException, ConcurrentTransactionDetectedException;

    void removeElement(String bucketName, String id) throws BucketDoesNotExistException, ElementDoesNotExistException;

    boolean elementExists(String bucketName, String elementId) throws BucketDoesNotExistException;

    void updateElement(VersionedElement toUpdate) throws BucketDoesNotExistException, ElementDoesNotExistException,
            ConcurrentTransactionDetectedException;

    List<Element> filterElements(BucketQuery query);

    long getNumberOfElements(String bucketName) throws BucketDoesNotExistException;
}