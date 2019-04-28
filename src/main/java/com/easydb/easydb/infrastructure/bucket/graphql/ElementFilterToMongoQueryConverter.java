package com.easydb.easydb.infrastructure.bucket.graphql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ElementFilterToMongoQueryConverter {

    Query transform(Optional<ElementFilter> elementFilter) {
        Query mongoQuery = new Query();
        return elementFilter.map(filter -> {
            mongoQuery.addCriteria(transformRecursively(elementFilter.get()));
            return mongoQuery;
        }).orElse(mongoQuery);
    }

    private Criteria transformRecursively(ElementFilter rootFilter) {
        validate(rootFilter);

        Criteria rootCriteria = new Criteria();
        if (rootFilter.getAnd() != null) {
            List<Criteria> andCriteries = rootFilter.getAnd().stream()
                    .map(this::transformRecursively)
                    .collect(Collectors.toList());

            Criteria[] andCriteriesArray = new Criteria[andCriteries.size()];
            andCriteries.toArray(andCriteriesArray);
            rootCriteria.andOperator(andCriteriesArray);
        } else if (rootFilter.getOr() != null) {
            List<Criteria> orCriteries = rootFilter.getOr().stream()
                    .map(this::transformRecursively)
                    .collect(Collectors.toList());

            Criteria[] orCriteriesArray = new Criteria[orCriteries.size()];
            orCriteries.toArray(orCriteriesArray);
            rootCriteria.orOperator(orCriteriesArray);
        } else if (rootFilter.getFieldsFilters() != null) {
            List<Map<String, String>> allMatchCriteries = rootFilter.getFieldsFilters().stream()
                    .map(this::convertToMap)
                    .collect(Collectors.toList());

            rootCriteria = Criteria.where("fields").all(allMatchCriteries);
        }
        return rootCriteria;
    }

    private void validate(ElementFilter rootFilter) {
        ElementFilter.validate(rootFilter);
    }

    private Map<String, String> convertToMap(FieldFilter filter) {
        Map<String, String> map = new HashMap<>();
        map.put("name", filter.getName());
        map.put("value", filter.getValue());
        return map;
    }
}
