package com.lightspeedhq.model;

public record Join(String type, Source source, Condition onCondition) {
    @Override
    public String toString() {
        return type + " JOIN " + source + " ON " + onCondition;
    }
}

