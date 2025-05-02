package com.lightspeedhq.model;

public record Sort(String column, boolean ascending) {
    @Override
    public String toString() {
        return column + (ascending ? " ASC " : " DESC ");
    }
}

