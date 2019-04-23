package com.easydb.easydb.infrastructure.bucket.graphql;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

class ElementEventsFilter {

    static boolean filter(GraphQlElementEvent elementEvent, Optional<ElementFilter> eventFilter) {
        return eventFilter.map((composedFilter) -> {
            validate(composedFilter);
            return elementMatchQuery(composedFilter, elementEvent.getElement());
        }).orElse(true);
    }

    private static boolean elementMatchQuery(ElementFilter filter, GraphQlElement element) {
        if (filter.getAnd() != null) {
            return filter.getAnd().stream().allMatch(andFilter -> elementMatchQuery(andFilter, element));
        } else if (filter.getOr() != null) {
            return filter.getOr().stream().anyMatch(orFilter -> elementMatchQuery(orFilter, element));
        } else {
            Set<GraphQlField> fieldsSet = new HashSet<>(element.getFields());
            return filter.getFieldsFilters().stream()
                    .allMatch(fieldFilter -> fieldsSet.contains(new GraphQlField(fieldFilter.getName(), fieldFilter.getValue())));
        }
    }

    private static void validate(ElementFilter rootFilter) {
        long operatorCount = Stream.of(rootFilter.getAnd() != null, rootFilter.getOr() != null, rootFilter.getFieldsFilters() != null)
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
