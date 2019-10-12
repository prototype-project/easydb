package com.easydb.easydb.api.space;

import com.easydb.easydb.domain.space.Space;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class SpaceDetailsDto {
    private final List<String> buckets;

    @JsonCreator
    private SpaceDetailsDto(@JsonProperty("buckets") List<String> buckets) {
        this.buckets = buckets;
    }

    static SpaceDetailsDto of(Space space) {
        return new SpaceDetailsDto(new ArrayList<>(space.getBuckets()));
    }

    public List<String> getBuckets() {
        return buckets;
    }
}
