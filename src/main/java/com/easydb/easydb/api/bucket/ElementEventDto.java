package com.easydb.easydb.api.bucket;

import com.easydb.easydb.domain.bucket.ElementEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElementEventDto {
    private final ElementQueryDto element;
    private final ElementEvent.Type type;

    @JsonCreator
    private ElementEventDto(
            @JsonProperty("element") ElementQueryDto element,
            @JsonProperty("type") ElementEvent.Type type) {
        this.element = element;
        this.type = type;
    }

    public ElementQueryDto getElement() {
        return element;
    }

    public ElementEvent.Type getType() {
        return type;
    }

    static ElementEventDto of(ElementEvent domainEvent) {
        return new ElementEventDto(ElementQueryDto.of(domainEvent.getElement()), domainEvent.getType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementEventDto)) return false;
        ElementEventDto that = (ElementEventDto) o;
        return element.equals(that.element) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, type);
    }
}
