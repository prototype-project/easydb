package com.easydb.easydb.domain.bucket;

import java.util.List;

public interface BucketService {
    boolean bucketExists(BucketName bucketName);

    void removeBucket(BucketName bucketName);

    void createBucket(BucketName bucketName);

    void addElement(Element element);

    Element getElement(BucketName bucketName, String id);

    void removeElement(BucketName bucketName, String elementId);

    boolean elementExists(BucketName bucketName, String elementId);

    void updateElement(Element toUpdate);

    List<Element> filterElements(BucketQuery query);

    long getNumberOfElements(BucketName bucketName);
}
