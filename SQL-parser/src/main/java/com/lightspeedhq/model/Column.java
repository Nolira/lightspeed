package com.lightspeedhq.model;

public record Column(String name, String alias, Query subQuery) {
    @Override
    public String toString() {
        String columnString = "";
        if (subQuery != null) {
            String subQueryString = subQuery.toSql();
            subQueryString = subQueryString.substring(0, subQueryString.length() - 1); // remove final ; for subqueries
            columnString = "(" + subQueryString + ")";
        } else {
            columnString = name;
        }
        return columnString + (alias == null ? "" : " AS " + alias);
    }
}
