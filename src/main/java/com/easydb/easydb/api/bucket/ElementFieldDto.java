package com.easydb.easydb.api.bucket;

import com.easydb.easydb.domain.bucket.ElementField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.hibernate.validator.constraints.NotEmpty;

public class ElementFieldDto {
    @NotEmpty
    private final String name;

    @NotEmpty
    private final String value;

    @JsonCreator
    ElementFieldDto(
            @JsonProperty("name") String name,
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

    public ElementField toDomain() {
        return ElementField.of(name, value);
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
            ElementFieldDto other = (ElementFieldDto) obj;
            return Objects.equals(other.name, this.name) && Objects.equals(other.value, this.value);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
