package com.easydb.easydb.domain.space;

import java.util.HashSet;
import java.util.Set;

public class Space {
    private final String name;
    private final Set<String> buckets;

    private Space(String name) {
        this.name = name;
        this.buckets = new HashSet<>();
    }

    private Space(String name, Set<String> buckets) {
        this.name = name;
        this.buckets = buckets;
    }

    public String getName() {
        return name;
    }

    public Set<String> getBuckets() {
        return buckets;
    }

    public static Space of(String name) {
        return new Space(name);
    }

    public static Space of(String name, Set<String> buckets) {
        return new Space(name, buckets);
    }
}
