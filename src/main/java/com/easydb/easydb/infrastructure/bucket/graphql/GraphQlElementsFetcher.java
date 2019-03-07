package com.easydb.easydb.infrastructure.bucket.graphql;

import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementField;
import graphql.ExecutionResult;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GraphQlElementsFetcher {
    private final GraphQlProvider graphQlProvider;

    public GraphQlElementsFetcher(GraphQlProvider graphQlProvider) {
        this.graphQlProvider = graphQlProvider;
    }

    public List<Element> elements(BucketQuery query) {
        ExecutionResult executionResult = graphQlProvider.graphQL(query)
                .execute(query.getQuery().orElse(Query.DEFAULT_GRAPHQL_QUERY));

        if (executionResult.getErrors().size() > 0) {
            throw new QueryValidationException(String.format("Query validation error: `%s`", executionResult.getErrors().get(0).getMessage()));
        }
        return convertToDomainElement(executionResult.getData(), query);

    }

    private List<Element> convertToDomainElement(LinkedHashMap result, BucketQuery query) {
        return extractElements(result).stream().map(element -> {
            String id = extractId(element);
            List<ElementField> elementFields = extractFields(element).stream()
                    .map(this::convertToField)
                    .collect(Collectors.toList());
            return Element.of(id, query.getBucketName(), elementFields);
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<LinkedHashMap> extractElements(LinkedHashMap result) {
        return (List<LinkedHashMap>) result.get("elements");
    }

    @SuppressWarnings("unchecked")
    private List<LinkedHashMap> extractFields(LinkedHashMap element) {
        return (List<LinkedHashMap>) element.get("fields");
    }

    private String extractId(LinkedHashMap element) {
        return (String) element.get("id");
    }

    private ElementField convertToField(LinkedHashMap field) {
        return new ElementField((String) field.get("name"), (String) field.get("value"));
    }

}
