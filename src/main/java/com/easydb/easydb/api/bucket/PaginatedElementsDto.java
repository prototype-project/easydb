package com.easydb.easydb.api.bucket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PaginatedElementsDto {
    private final String next;

    private final List<ElementQueryDto> results;

    @JsonCreator
    private PaginatedElementsDto(@JsonProperty("nextPageLink") String nextPageLink,
                                 @JsonProperty("results") List<ElementQueryDto> results) {
        this.next = nextPageLink;
        this.results = results;
    }

    public String getNext() {
        return next;
    }

    public List<ElementQueryDto> getResults() {
        return results;
    }

    public static PaginatedElementsDto of(String nextPageLink, List<ElementQueryDto> results) {
        return new PaginatedElementsDto(nextPageLink, results);
    }
}
