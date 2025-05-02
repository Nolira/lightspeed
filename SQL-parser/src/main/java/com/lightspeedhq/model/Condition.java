package com.lightspeedhq.model;

public record Condition(String expression) {
    @Override
    public String toString() {
        return expression;
    }
}

