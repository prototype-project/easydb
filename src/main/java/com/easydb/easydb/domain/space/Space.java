package com.easydb.easydb.domain.space;

public class Space {
    private final String name;

    private Space(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Space of(String name) {
        return new Space(name);
    }
}
