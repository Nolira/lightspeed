package com.lightspeedhq.util;

import com.lightspeedhq.model.Column;
import com.lightspeedhq.model.Condition;
import com.lightspeedhq.model.Join;
import com.lightspeedhq.model.Query;
import com.lightspeedhq.model.Sort;
import com.lightspeedhq.model.Source;

import java.util.ArrayList;
import java.util.List;

/**
 * This parser for SQL SELECT statements creates a representation of the query.
 */
public class SqlSelectParser {
    private final String[] tokens;
    private int pos = 0;

    /**
     * Constructs an instance of SqlSelectParser with the provided SQL string.
     *
     * @param sql The SQL SELECT string to be parsed.
     */
    public SqlSelectParser(String sql) {
        this.tokens = tokenize(sql);
    }

    /**
     * Parses the SQL SELECT statement and returns a Query object representing it.
     *
     * @return A Query object containing information about the parsed query.
     */
    public Query parse() {
        expect("select");
        moveNext();
        List<Column> columns = parseColumns();

        expect("from");
        moveNext();
        List<Source> sources = parseSources();

        List<Join> joins = new ArrayList<>();
        while (peekIs("join") || peekIsJoinType()) {
            joins.add(parseJoin());
        }

        Condition whereClauses = null;
        if (peekIs("where")) {
            moveNext();
            whereClauses = parseConditions();
        }

        List<String> groupBy = new ArrayList<>();
        if (peekIs("group")) {
            moveNext();
            expect("by");
            moveNext();
            groupBy = parseIdentifiers();
        }

        Condition havingClauses = null;
        if (peekIs("having")) {
            moveNext();
            havingClauses = parseConditions();
        }

        List<Sort> sort = new ArrayList<>();
        if (peekIs("order")) {
            moveNext();
            expect("by");
            moveNext();
            sort = parseSort();
        }

        Integer limit = null;
        if (peekIs("limit")) {
            moveNext();
            limit = Integer.parseInt(getCurrent());
            moveNext();
        }

        Integer offset = null;
        if (peekIs("offset")) {
            moveNext();
            offset = Integer.parseInt(getCurrent());
        }

        return new Query(columns, sources, joins, whereClauses, groupBy, havingClauses, sort, limit, offset);
    }

    private List<Column> parseColumns() {
        List<Column> columns = new ArrayList<>();
        String columnName = null;
        while (!peekIs("from")) {
            Query subquery = null;
            if (peekIs("(")) {
                columnName = null;
                moveNext();
                subquery = parse();
                expect(")");
                moveNext();
            } else {
                columnName = "";
                while (!peekIs(",", "from", "as")) {
                    columnName = columnName + getCurrent();
                    moveNext();
                }
            }
            String columnAlias = getAlias();
            if (peekIs(",")) moveNext();
            columns.add(new Column(columnName, columnAlias, subquery));
        }
        return columns;
    }

    private List<Source> parseSources() {
        List<Source> sources = new ArrayList<>();
        while (peekIsNotEnd() && !peekIsKeyword() && !(peekIs(")") || peekIsJoinType())) {
            sources.add(parseSource());
            if (peekIsNotEnd() && peekIsKeyword() || peekIs(")") || peekIsJoinType()) {
            } else moveNext();
        }
        return sources;
    }

    private Source parseSource() {
        if (peekIs("(")) {
            moveNext();
            Query subquery = parse();
            expect(")");
            moveNext();
            String alias = getAlias();
            return new Source(null, alias, subquery);
        } else {
            String name = getCurrent();
            moveNext();
            String alias = getAlias();
            return new Source(name, alias, null);
        }
    }

    private Join parseJoin() {
        String type = "";
        if (peekIsJoinType()) {
            type = type + getCurrent();
            moveNext();
            if (peekIs("outer")) { // optional 'outer'
                type = type + " " + getCurrent();
                moveNext();
            }
        }
        expect("join");
        moveNext();
        Source source = parseSource();
        expect("on");
        moveNext();
        Condition onExpr = parseConditions();
        return new Join(type.trim(), source, onExpr);
    }

    // TODO parse subqueries in conditions, it requires to rewrite Condition record, probably making it some kind of tree
    private Condition parseConditions() {
        String condition = "";
        int parentesisCount = 0;
        while (peekIsNotEnd() && !(peekIs("where", "group", "having", "order", "limit", "offset", ";", "join") || peekIsJoinType())) {
            if (peekIs(")") && parentesisCount == 0) {
                break;
            }
            if (peekIs("(")) {
                parentesisCount++;
                condition = condition.trim() + getCurrent();
            } else if (peekIsConditionalKeyword()) {
                condition = condition.trim() + " " + getCurrent() + " ";
                moveNext();
                if (peekIs("(")) {
                    parentesisCount++;
                    condition = condition + getCurrent();
                    moveNext();
                }
                continue;
            } else if (peekIs(")") && (parentesisCount > 0)) {
                parentesisCount--;
                condition = condition.trim() + getCurrent() + " ";
            } else {
                condition = condition + getCurrent() + " ";
            }
            moveNext();
        }
        return new Condition(condition.trim());
    }

    private List<String> parseIdentifiers() {
        List<String> ids = new ArrayList<>();
        while (peekIsNotEnd() && !peekIs("having", "order", "limit", "offset", ";", ")")) {
            String val = getCurrent();
            moveNext();
            if (",".equals(val)) continue;
            ids.add(val);
        }
        return ids;
    }

    private List<Sort> parseSort() {
        List<Sort> sortList = new ArrayList<>();
        while (peekIsNotEnd() && !peekIs("limit", "offset", ";", ")")) {
            String column = getCurrent();
            moveNext();
            boolean asc = true;
            if (peekIs("asc")) {
                asc = true;
            } else if (peekIs("desc")) {
                asc = false;
            }
            sortList.add(new Sort(column, asc));
            moveNext();
            if (peekIs(",")) moveNext();
        }
        return sortList;
    }

    private String[] tokenize(String sqlString) {
        return sqlString.replaceAll("(?i)([(),;])", " $1 ").replaceAll("\\s+", " ").trim().split(" ");
    }

    private boolean peekIs(String... expected) {
        if (pos >= tokens.length) return false;
        for (String e : expected) {
            if (tokens[pos].equalsIgnoreCase(e)) return true;
        }
        return false;
    }

    private boolean peekIsKeyword() {
        return peekIs("from", "where", "group", "having", "order", "limit", "offset", "join", "on");
    }

    private boolean peekIsConditionalKeyword() {
        return peekIs("=", "!=", "<", ">", "<=", ">=", "is", "like", "in", "and", "or", "not");
    }

    private boolean peekIsNotEnd() {
        return pos < tokens.length;
    }

    private boolean peekIsJoinType() {
        return peekIs("left", "right", "full", "inner");
    }

    private String getCurrent() {
        return tokens[pos];
    }

    // TODO add support for aliases with spaces e.g. "like that" [or that]
    private String getAlias() {
        if (peekIs("as")) {
            moveNext();
            String alias = getCurrent();
            moveNext();
            return alias;
        }
        if (peekIsNotEnd() && !peekIsKeyword() && !(peekIs(",", "on", "join", "where", ")") || peekIsJoinType())) {
            String alias = getCurrent();
            moveNext();
            return alias;
        }
        return null;
    }

    private void moveNext() {
        pos++;
    }

    private void expect(String expected) {
        if (!peekIs(expected)) throw new RuntimeException("Expected '" + expected + "' at position " + pos);
    }
}

