package com.easydb.easydb.domain;

import java.util.List;
import java.util.stream.Collectors;

public class ElementQueryDto {
    private final String id;
    private final String name;
    private final List<ElementFieldDto> fields;

    private ElementQueryDto(String id, String name, List<ElementFieldDto> fields) {
        this.id = id;
        this.name = name;
        this.fields = fields;
    }

    static ElementQueryDto of(BucketElement element) {
        return new ElementQueryDto(
                element.get
                element.getName(),
                element.getFields().stream()
                        .map(it -> ElementFieldDto.of(it.getName(), it.getValue()))
                        .collect(Collectors.toList())
        )
    }
}
