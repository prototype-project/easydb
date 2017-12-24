package com.easydb.easydb.api;

import com.easydb.easydb.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ElementQueryApiDto createElement(
            @PathVariable("bucketName") String bucketName,
            @RequestBody ElementCreateApiDto toCreate) {
        ElementQueryDto createdElement = space.addElement(toCreate.toDomainDto());
        return ElementQueryApiDto.from(createdElement);
    }

    @DeleteMapping(path = "/buckets/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteElement(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        space.removeElement(bucketName, elementId);
    }
}
