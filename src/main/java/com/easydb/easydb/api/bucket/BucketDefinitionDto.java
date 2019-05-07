package com.easydb.easydb.api.bucket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;

public class BucketDefinitionDto {

    @NotEmpty
    private final String bucketName;

    @JsonCreator
    public BucketDefinitionDto(@JsonProperty("bucketName") String bucketName) {
        this.bucketName = bucketName;
    }

    public String getName() {
        return bucketName;
    }
}
