package com.easydb.easydb.domain.bucket.transactions;

import com.easydb.easydb.domain.bucket.BucketName;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementService;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionalElementService implements ElementService {

    private final BucketRepository bucketRepository;

    public TransactionalElementService(
            BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    public void addElement(Element element) {
        bucketRepository.insertElement(
                Element.of(element.getId(), element.getBucketName(), element.getFields()));
    }

    public VersionedElement removeElement(BucketName bucketName, String elementId) {
        return bucketRepository.removeElement(bucketName, elementId);
    }

    public void updateElement(VersionedElement toUpdate) {
        VersionedElement withBucketRenamed = toUpdate.getVersion()
                .map(v -> VersionedElement.of(toUpdate.getId(), toUpdate.getBucketName(),
                        toUpdate.getFields(), v))
                .orElseGet(() -> VersionedElement.of(toUpdate.getId(), toUpdate.getBucketName(),
                        toUpdate.getFields()));
        bucketRepository.updateElement(withBucketRenamed);
    }

    public VersionedElement getElement(BucketName bucketName, String id) {
        VersionedElement versionedElement = bucketRepository.getElement(bucketName, id);
        return VersionedElement.of(versionedElement.getId(), bucketName,
                versionedElement.getFields(), versionedElement.getVersionOrThrowErrorIfEmpty());
    }

    public VersionedElement getElement(BucketName bucketName, String id, long version) {
        VersionedElement versionedElement = bucketRepository.getElement(bucketName, id, version);
        return VersionedElement.of(versionedElement.getId(), bucketName,
                versionedElement.getFields(), versionedElement.getVersionOrThrowErrorIfEmpty());
    }

    public long getNumberOfElements(BucketName bucketName) {
        return bucketRepository.getNumberOfElements(bucketName);
    }

    public List<Element> filterElements(BucketQuery query) {
        return bucketRepository.filterElements(query).stream()
                .map(it -> Element.of(it.getId(), query.getBucketName(), it.getFields()))
                .collect(Collectors.toList());
    }

    public boolean elementExists(BucketName bucketName, String elementId) {
        return bucketRepository.elementExists(bucketName, elementId);
    }
}