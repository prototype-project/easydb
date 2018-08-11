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
        Space space = spaceRepository.get(spaceName);
        space.getBuckets().add(element.getBucketName());
        spaceRepository.update(space);
        bucketRepository.insertElement(
                Element.of(element.getId(), getBucketNameAccordinglyToSpace(element.getBucketName()), element.getFields()));
    }

    public void removeElement(String bucketName, String elementId) {
        bucketRepository.removeElement(getBucketNameAccordinglyToSpace(bucketName), elementId);
    }

    public void updateElement(Element toUpdate) {
        bucketRepository.updateElement(
                Element.of(toUpdate.getId(), getBucketNameAccordinglyToSpace(toUpdate.getBucketName()), toUpdate.getFields()));
    }

    public VersionedElement getElement(String bucketName, String id) {
        VersionedElement versionedElement = bucketRepository.getElement(getBucketNameAccordinglyToSpace(bucketName), id);
        return VersionedElement.of(versionedElement.getId(), bucketName,
                versionedElement.getFields(), versionedElement.getVersion());
    }

    long getNumberOfElements(String bucketName) {
        return bucketRepository.getNumberOfElements(getBucketNameAccordinglyToSpace(bucketName));
    }

    List<Element> filterElements(BucketQuery query) {
        return bucketRepository.filterElements(rebuildToProperSpaceName(query)).stream()
                .map(it -> Element.of(it.getId(), query.getBucketName(), it.getFields()))
                .collect(Collectors.toList());
    }

    boolean elementExists(String bucketName, String elementId) {
        return bucketRepository.elementExists(getBucketNameAccordinglyToSpace(bucketName), elementId);
    }

    String getBucketNameAccordinglyToSpace(String bucketName) {
        return spaceName + ":" + bucketName;
    }

    private BucketQuery rebuildToProperSpaceName(BucketQuery query) {
        return query.rename(getBucketNameAccordinglyToSpace(query.getBucketName()));
    }
}