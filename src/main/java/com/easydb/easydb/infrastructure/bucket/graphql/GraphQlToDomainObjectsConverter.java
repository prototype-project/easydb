package com.easydb.easydb.infrastructure.bucket.graphql;

import com.easydb.easydb.domain.bucket.BucketName;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementEvent;
import com.easydb.easydb.domain.bucket.ElementField;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class GraphQlToDomainObjectsConverter {
    private final BucketName bucketName;

    private GraphQlToDomainObjectsConverter(BucketName bucketName) {
        this.bucketName = bucketName;
    }

    static GraphQlToDomainObjectsConverter of(BucketName bucketName) {
        return new GraphQlToDomainObjectsConverter(bucketName);
    }

    ElementEvent convertToDomainElementEvent(Map map) {
        Map root = (Map)map.get("elementsEvents");
        ElementEvent.Type type = ElementEvent.Type.valueOf((String)root.get("type"));
        Element element = convertToSingleDomainElement((Map)root.get("element"));
        return new ElementEvent(element, type);
    }

    List<Element> convertToDomainElements(Map result) {
        return extractElements(result).stream().map(this::convertToSingleDomainElement).collect(Collectors.toList());
    }

    private Element convertToSingleDomainElement(Map element) {
            String id = extractId(element);
            List<ElementField> elementFields = extractFields(element).stream()
                    .map(this::convertToField)
                    .collect(Collectors.toList());
            return Element.of(id, bucketName, elementFields);
    }

    @SuppressWarnings("unchecked")
    private List<Map> extractElements(Map result) {
        return (List<Map>) result.get("elements");
    }

    @SuppressWarnings("unchecked")
    private List<Map> extractFields(Map element) {
        return (List<Map>) element.get("fields");
    }

    private String extractId(Map element) {
        return (String) element.get("id");
    }

    private ElementField convertToField(Map field) {
        return new ElementField((String) field.get("name"), (String) field.get("value"));
    }
}
