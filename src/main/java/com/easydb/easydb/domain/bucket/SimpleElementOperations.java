package com.easydb.easydb.domain.bucket;

import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.space.SpaceRepository;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleElementOperations {

    private final String spaceName;
    private final SpaceRepository spaceRepository;
    private final BucketRepository bucketRepository;

    public SimpleElementOperations(
            String spaceName,
            SpaceRepository spaceRepository,
            BucketRepository bucketRepository) {
        this.spaceName = spaceName;
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
    }

    public void addElement(Element element) {
        // TODO race conditions with remove
        Space space = spaceRepository.get(spaceName);
        if (!space.getBuckets().contains(element.getBucketName())) {
            space.getBuckets().add(element.getBucketName());
            spaceRepository.update(space);
        }
        bucketRepository.insertElement(
                Element.of(element.getId(), getBucketNameAccordinglyToSpace(element.getBucketName()), element.getFields()));
    }

    public void removeElement(String bucketName, String elementId) {
        bucketRepository.removeElement(getBucketNameAccordinglyToSpace(bucketName), elementId);
    }

    public void updateElement(VersionedElement toUpdate) {
        VersionedElement withBucketRenamed = toUpdate.getVersion()
                .map(v -> VersionedElement.of(toUpdate.getId(), getBucketNameAccordinglyToSpace(toUpdate.getBucketName()),
                        toUpdate.getFields(), v))
                .orElseGet(() -> VersionedElement.of(toUpdate.getId(), getBucketNameAccordinglyToSpace(toUpdate.getBucketName()),
                        toUpdate.getFields()));
        bucketRepository.
                updateElement(withBucketRenamed);
    }

    public VersionedElement getElement(String bucketName, String id) {
        VersionedElement versionedElement = bucketRepository.getElement(getBucketNameAccordinglyToSpace(bucketName), id);
        return VersionedElement.of(versionedElement.getId(), bucketName,
                versionedElement.getFields(), versionedElement.getVersionOrThrowErrorIfEmpty());
    }

    public VersionedElement getElement(String bucketName, String id, long version) {
        VersionedElement versionedElement = bucketRepository.getElement(getBucketNameAccordinglyToSpace(bucketName), id, version);
        return VersionedElement.of(versionedElement.getId(), bucketName,
                versionedElement.getFields(), versionedElement.getVersionOrThrowErrorIfEmpty());
    }

    long getNumberOfElements(String bucketName) {
        return bucketRepository.getNumberOfElements(getBucketNameAccordinglyToSpace(bucketName));
    }

    List<VersionedElement> filterElements(BucketQuery query) {
        return bucketRepository.filterElements(rebuildToProperSpaceName(query)).stream()
                .map(it -> VersionedElement.of(it.getId(), query.getBucketName(), it.getFields()))
                .collect(Collectors.toList());
    }

    boolean elementExists(String bucketName, String elementId) {
        return bucketRepository.elementExists(getBucketNameAccordinglyToSpace(bucketName), elementId);
    }

    private String getBucketNameAccordinglyToSpace(String bucketName) {
        return NamesResolver.resolve(spaceName, bucketName);
    }

    private BucketQuery rebuildToProperSpaceName(BucketQuery query) {
        return BucketQuery.of(getBucketNameAccordinglyToSpace(query.getBucketName()), query.getLimit(), query.getOffset());
    }
}