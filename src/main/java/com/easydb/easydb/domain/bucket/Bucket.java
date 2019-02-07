package com.easydb.easydb.domain.bucket;

public class Bucket {
    private final String name;

    private Bucket(String name) {
        this.name = name;
    }

    public static Bucket of(String name) {
        return new Bucket(name);
    }

    String getName() {
        return name;
    }
}
