package com.lightspeedhq.model;

public record Source(String name, String alias, Query subQuery) {
    @Override
    public String toString() {
        if (subQuery != null) {
            String s =  subQuery.toSql();
            s = s.substring(0, s.length() - 1); // remove final ; for subqueries
            return "(" + s + ")" + (alias != null ? " AS " + alias : "");
        }
        return name + (alias != null ? " AS " + alias : "");
    }
}

