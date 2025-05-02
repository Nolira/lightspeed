package com.lightspeedhq.model;

import java.util.List;

public record Query(List<Column> columns,
                    List<Source> fromSources,
                    List<Join> joins,
                    Condition whereClauses,
                    List<String> groupByColumns,
                    Condition havingClauses,
                    List<Sort> sortColumns,
                    Integer limit,
                    Integer offset) {
    @Override
    public String toString() {
        return
                "Parsed Query:\n" +
                        "Columns: " + columns() + "\n" +
                        "From: " + fromSources() + "\n" +
                        "Joins: " + joins() + "\n" +
                        "Where: " + whereClauses() + "\n" +
                        "GroupBy: " + groupByColumns() + "\n" +
                        "HavingBy: " + havingClauses() + "\n" +
                        "Sort: " + sortColumns() + "\n" +
                        "Limit: " + limit() + "\n" +
                        "Offset: " + offset();
    }

    public String toSql() {
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ")
                .append(String.join(", ", columns.stream().map(Column::toString).toList()))
                .append(" ");

        if (!fromSources.isEmpty()) {
            sb.append("FROM ")
                    .append(String.join(", ", fromSources.stream().map(Source::toString).toList()))
                    .append(" ");
        }

        for (Join join : joins) {
            sb.append(join).append(" ");
        }

        if (whereClauses != null) {
            sb.append("WHERE ").append(whereClauses).append(" ");
        }

        if (!groupByColumns.isEmpty()) {
            sb.append("GROUP BY ")
                    .append(String.join(", ", groupByColumns))
                    .append(" ");
        }

        if (havingClauses != null) {
            sb.append("HAVING ").append(havingClauses).append(" ");
        }

        if (!sortColumns.isEmpty()) {
            sb.append("ORDER BY ");
            sb.append(String.join(", ", sortColumns.stream().map(Sort::toString).toList()));
            sb.append(" ");
        }

        if (limit != null) {
            sb.append("LIMIT ").append(limit);
            if (offset != null) {
                sb.append(" OFFSET ").append(offset);
            }
            sb.append(" ");
        }

        return sb.toString().trim() + ";";
    }
}