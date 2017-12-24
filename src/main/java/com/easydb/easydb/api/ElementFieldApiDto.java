package com.easydb.easydb.api;

import java.util.Objects;

public class ElementFieldApiDto implements Comparable<ElementFieldApiDto> {
    private String name;
    private String value;

    public ElementFieldApiDto(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ElementFieldApiDto() {}

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
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
