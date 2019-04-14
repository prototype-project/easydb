package com.easydb.easydb.infrastructure.bucket.graphql;

import com.easydb.easydb.domain.BucketName;
import com.easydb.easydb.domain.bucket.Bucket;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementField;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

class GraphQlToDomainElementConverter {
    private final BucketName bucketName;

    private GraphQlToDomainElementConverter(BucketName bucketName) {
        this.bucketName = bucketName;
    }

    static GraphQlToDomainElementConverter of(BucketName bucketName) {
        return new GraphQlToDomainElementConverter(bucketName);
    }

    List<Element> convertToDomainElements(LinkedHashMap result) {
        return extractElements(result).stream().map(element -> {
            String id = extractId(element);
            List<ElementField> elementFields = extractFields(element).stream()
                    .map(this::convertToField)
                    .collect(Collectors.toList());
            return Element.of(id, bucketName, elementFields);
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
