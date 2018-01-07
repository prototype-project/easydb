package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.infrastructure.space.UUIDProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1")
class SpaceController {

    private final Space space;
    private final UUIDProvider uuidProvider;

    private SpaceController(Space space, UUIDProvider uuidProvider) {
        this.space = space;
        this.uuidProvider = uuidProvider;
    }

    @DeleteMapping(path = "/buckets/{bucketName}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteBucket(@PathVariable("bucketName") String bucketName) {
        space.removeBucket(bucketName);
    }

    @PostMapping(path = "/buckets/{bucketName}")
    @ResponseStatus(value = HttpStatus.CREATED)
    ElementQueryApiDto addElement(
            @PathVariable("bucketName") String bucketName,
            @RequestBody ElementOperationApiDto toCreate) {
        Element element = toCreate.toDomain(uuidProvider.generateUUID(), bucketName);
        space.addElement(element);
        return ElementQueryApiDto.from(element);
    }

    @DeleteMapping(path = "/buckets/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteElement(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        space.removeElement(bucketName, elementId);
    }

    @PutMapping(path = "/buckets/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void updateElement(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId,
            @RequestBody ElementOperationApiDto toUpdate) {
        space.updateElement(toUpdate.toDomain(elementId, bucketName));
    }

    @GetMapping(path = "/buckets/{bucketName}")
    @ResponseStatus(value = HttpStatus.OK)
    List<ElementQueryApiDto> getAllElements(@PathVariable("bucketName") String bucketName) {
        return space.getAllElements(bucketName).stream()
                .map(ElementQueryApiDto::from)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/buckets/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    ElementQueryApiDto getElement(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        return ElementQueryApiDto.from(space.getElement(bucketName, elementId));
    }
}
