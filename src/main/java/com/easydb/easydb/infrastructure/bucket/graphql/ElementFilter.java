package com.easydb.easydb.infrastructure.bucket.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Stream;

public class ElementFilter {
    private List<FieldFilter> fieldsFilters;
    private List<ElementFilter> or;
    private List<ElementFilter> and;

    @JsonProperty("fieldsFilters")
    public List<FieldFilter> getFieldsFilters() {
        return fieldsFilters;
    }

    public void setFieldsFilters(List<FieldFilter> fieldsFilters) {
        this.fieldsFilters = fieldsFilters;
    }

    @JsonProperty("or")
    public List<ElementFilter> getOr() {
        return or;
    }

    public void setOr(List<ElementFilter> or) {
        this.or = or;
    }

    @JsonProperty("and")
    public List<ElementFilter> getAnd() {
        return and;
    }

    public void setAnd(List<ElementFilter> and) {
        this.and = and;
    }

    static void validate(ElementFilter elementFilter) {
        long operatorCount = Stream.of(elementFilter.getAnd() != null, elementFilter.getOr() != null, elementFilter.getFieldsFilters() != null)
                .filter(b -> b)
                .count();
        if (operatorCount < 1) {
            throw new QueryValidationException("Empty query or subquery");
        }
        if (operatorCount > 1) {
            throw new QueryValidationException("Query or subquery can only contain one of [`and`, `or`, `fieldsFilters`] operator. You cannot use them together");
        }
    }
}
