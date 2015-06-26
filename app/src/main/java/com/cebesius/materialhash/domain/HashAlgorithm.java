package com.cebesius.materialhash.domain;

import java.io.Serializable;

public class HashAlgorithm implements Comparable<HashAlgorithm>, Serializable {

    private final String name;
    private final String label;

    public HashAlgorithm(String name) {
        this.name = name;
        this.label = name;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int compareTo(HashAlgorithm another) {
        return label.compareTo(another.label);
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
