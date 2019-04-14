package com.easydb.easydb.domain.bucket;

import com.easydb.easydb.domain.BucketName;
import com.easydb.easydb.domain.bucket.transactions.VersionedElement;
import java.util.List;

public interface ElementService {

    void addElement(Element element);

    void removeElement(BucketName bucketName, String elementId);

    void updateElement(VersionedElement toUpdate);

    VersionedElement getElement(BucketName bucketName, String id);

    VersionedElement getElement(BucketName bucketName, String id, long version);

    long getNumberOfElements(BucketName bucketName);

    List<Element> filterElements(BucketQuery query);

    boolean elementExists(BucketName bucketName, String elementId);
}