package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.dto.ElementQueryDto;
import com.easydb.easydb.domain.space.Space;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1")
class SpaceController {

    private Space space;

    private SpaceController(Space space) {
        this.space = space;
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
        ElementQueryDto createdElement = space.addElement(toCreate.toCreateDto(bucketName));
        return ElementQueryApiDto.from(createdElement);
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
        space.updateElement(toUpdate.toUpdateDto(bucketName, elementId));
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
