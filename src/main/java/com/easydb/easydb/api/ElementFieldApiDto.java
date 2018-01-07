package com.easydb.easydb.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class ElementFieldApiDto implements Comparable<ElementFieldApiDto> {
    private final String name;
    private final String value;

    @JsonCreator
    ElementFieldApiDto(
            @JsonProperty("spaceName") String name,
            @JsonProperty("value") String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(ElementFieldApiDto o) {
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj.getClass() != this.getClass()) {
            return false;
        } else {
            ElementFieldApiDto other = (ElementFieldApiDto) obj;
            return Objects.equals(other.name, this.name) && Objects.equals(other.value, this.value);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
