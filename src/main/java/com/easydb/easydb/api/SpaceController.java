package com.easydb.easydb.api;

import com.easydb.easydb.domain.Space;
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
        space.createBucket(bucketDefinition.getName(), bucketDefinition.getFields());
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
