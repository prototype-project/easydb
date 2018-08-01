package com.easydb.easydb.domain.bucket;

import java.util.List;

public interface BucketService {
    boolean bucketExists(String bucketName);

    void removeBucket(String bucketName);

    void addElement(Element element);

    Element getElement(String bucketName, String id);

    void removeElement(String bucketName, String elementId);

    boolean elementExists(String bucketName, String elementId);

    void updateElement(Element toUpdate);

    List<Element> filterElements(BucketQuery query);

    long getNumberOfElements(String bucketName);
}
