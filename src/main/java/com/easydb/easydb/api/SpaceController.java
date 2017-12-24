package com.easydb.easydb.api;

import com.easydb.easydb.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1")
class SpaceController {

    Space space;

    SpaceController(Space space) {
        this.space = space;
    }

    @PostMapping(path = "/buckets")
    public ResponseEntity createBucket(@RequestBody BucketDefinitionDto bucketDefinition) {
        try {
            space.createBucket(bucketDefinition.getName(), bucketDefinition.getFields());
        } catch (BucketExistsException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/buckets/{bucketName}")
    public ResponseEntity deleteBucket(@PathVariable("bucketName") String bucketName) {
        try {
            space.removeBucket(bucketName);
        } catch (BucketDoesNotExistException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path = "/buckets/{bucketName}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ElementQueryApiDto addElement(
            @PathVariable("bucketName") String bucketName,
            @RequestBody ElementOperationApiDto toCreate) {
        ElementQueryDto createdElement = space.addElement(toCreate.toCreateDto());
        return ElementQueryApiDto.from(createdElement);
    }

    @DeleteMapping(path = "/buckets/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteElement(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        space.removeElement(bucketName, elementId);
    }

    @PutMapping(path = "/buckets/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void updateElement(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId,
            @RequestBody ElementOperationApiDto toUpdate) {
        space.updateElement(toUpdate.toUpdateDto());
    }

    @GetMapping(path = "/buckets/{bucketName}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<ElementQueryApiDto> getAllElements(@PathVariable("bucketName") String bucketName) {
        return space.getAllElements(bucketName).stream()
                .map(ElementQueryApiDto::from)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/buckets/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ElementQueryApiDto getElement(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        return ElementQueryApiDto.from(space.getElement(bucketName, elementId));
    }
}
