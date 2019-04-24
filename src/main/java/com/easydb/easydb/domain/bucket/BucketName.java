package com.easydb.easydb.domain.bucket;

import java.util.Objects;

public class BucketName {

    private final String spaceName;
    private final String name;

    public BucketName(String spaceName, String name) {
        this.spaceName = spaceName;
        this.name = name;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BucketName that = (BucketName) o;
        return Objects.equals(spaceName, that.spaceName) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spaceName, name);
    }
}
