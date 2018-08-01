package com.easydb.easydb.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class PaginatedElementsApiDto {
    private final String next;

    @NotNull
    @Valid
    private final List<ElementQueryApiDto> results;

    @JsonCreator
    private PaginatedElementsApiDto(@JsonProperty("nextPageLink") String nextPageLink,
                                    @JsonProperty("results") List<ElementQueryApiDto> results) {
        this.next = nextPageLink;
        this.results = results;
    }

    public String getNext() {
        return next;
    }

    public List<ElementQueryApiDto> getResults() {
        return results;
    }

    static PaginatedElementsApiDto of(String nextPageLink, List<ElementQueryApiDto> results) {
        return new PaginatedElementsApiDto(nextPageLink, results);
    }
}
