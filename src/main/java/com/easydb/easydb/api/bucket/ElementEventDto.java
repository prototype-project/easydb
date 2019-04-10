package com.easydb.easydb.api.bucket;

import com.easydb.easydb.domain.bucket.ElementEvent;

public class ElementEventDto {
    private final ElementQueryDto element;
    private final ElementEvent.Type type;

    private ElementEventDto(ElementQueryDto element,
                            ElementEvent.Type type) {
        this.element = element;
        this.type = type;
    }

    public static ElementEventDto of(ElementEvent elementEvent) {
        return new ElementEventDto(ElementQueryDto.of(elementEvent.getElement()), elementEvent.getType());
    }

    public ElementQueryDto getElement() {
        return element;
    }

    public ElementEvent.Type getType() {
        return type;
    }
}
