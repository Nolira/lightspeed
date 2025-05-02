package com.lightspeedhq.util;

import com.lightspeedhq.model.Query;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlSelectParserTest {
    @Test
    void shouldParseEnumerationOfSampleFieldsExplicitly() {
        Query query;

        query = new SqlSelectParser("SELECT author.name, count(book.id), sum(book.cost) FROM author").parse();
        assertEquals(3, query.columns().size());

        query = new SqlSelectParser("""
                SELECT 
                    id user_id, 
                    first_name user_first_name, 
                    last_name user_last_name 
                FROM users;
                """).parse();
        assertEquals(3, query.columns().size());
    }

    @Test
    void shouldParseImplicitJoin() {
        Query query;

        query = new SqlSelectParser("select * from A,B,C").parse();
        assertEquals(3, query.fromSources().size());

        query = new SqlSelectParser("""
                SELECT
                    customers.customer_id AS cust_id,
                    customers.first_name AS cust_first_name,
                    orders.order_id AS order_id,
                    orders.order_date AS order_date,
                    order_details.product_id AS pid,
                    order_details.quantity AS order_quantity
                FROM customers, orders, order_details
                WHERE customers.customer_id = orders.customer_id
                    AND orders.order_id = order_details.order_id;
                """).parse();
        assertEquals(3, query.fromSources().size());
    }

    @Test
    void shouldParseExplicitJoin() {
        Query query = new SqlSelectParser("select * from A left join B on (a.id = b.id)").parse();
        assertEquals(1, query.joins().size());

        query = new SqlSelectParser("""
                SELECT 
                    customers.customer_id,
                    customers.first_name,
                    orders.order_id,
                    products.product_id,
                    products.name AS product_name
                FROM 
                    customers
                JOIN 
                    orders ON customers.customer_id = orders.customer_id
                JOIN 
                    products ON orders.product_id = products.product_id;
                """).parse();
        assertEquals(1, query.fromSources().size());
        assertEquals(2, query.joins().size());
    }

    @Test
    void shouldParseFilterConditions() {
        Query query = new SqlSelectParser("select * from A").parse();
        assertNull(query.whereClauses());

        query = new SqlSelectParser("select * from A where a = 1 and b > 100").parse();
        assertEquals("a = 1 and b > 100", query.whereClauses().toString());
    }

    @Test
    void shouldParseSubqueries() {
        Query query = new SqlSelectParser("select * from (select * from A) as a_alias").parse();
        assertNotNull(query.fromSources().get(0).subQuery());
        assertEquals("select * from A;".toLowerCase(), query.fromSources().get(0).subQuery().toSql().toLowerCase());
        assertEquals("a_alias", query.fromSources().get(0).alias());

        query = new SqlSelectParser("""
                SELECT 
                    customer_id,
                    first_name,
                    (
                        SELECT COUNT(*) FROM orders WHERE customer_id = c.customer_id
                    ) AS order_count
                FROM 
                    customers c;
                """).parse();
        assertNotNull(query.columns().get(2).subQuery());
    }

    @Test
    void shouldParseGrouping() {
        Query query = new SqlSelectParser("select * from A group by id, name").parse();
        assertEquals(2, query.groupByColumns().size());

        query = new SqlSelectParser("select * from A").parse();
        assertTrue(query.groupByColumns().isEmpty());

        query = new SqlSelectParser("""
                SELECT 
                    product_category,
                    SUM(sale_amount) AS total_sales
                FROM 
                    sales
                GROUP BY 
                    product_category
                HAVING 
                    SUM(sale_amount) > 1000;
                """).parse();
        assertEquals(1, query.groupByColumns().size());
        assertEquals("SUM(sale_amount) > 1000", query.havingClauses().toString());
    }

    @Test
    void shouldParseSorting() {
        Query query = new SqlSelectParser("select * from A order by id, name").parse();
        assertEquals(2, query.sortColumns().size());

        query = new SqlSelectParser("select * from A").parse();
        assertTrue(query.sortColumns().isEmpty());

        query = new SqlSelectParser("""
                SELECT 
                    customer_id,
                    MAX(order_date) AS last_order_date
                FROM 
                    orders
                GROUP BY 
                    customer_id
                ORDER BY 
                    last_order_date DESC;
                """).parse();
        assertEquals(1, query.sortColumns().size());
        assertFalse(query.sortColumns().get(0).ascending());

        query = new SqlSelectParser("""
                SELECT p.product_name
                FROM (
                    SELECT product_id, MAX(price) AS max_price
                    FROM products
                    GROUP BY product_id
                ) p
                JOIN products ON p.product_id = products.product_id
                ORDER BY p.max_price DESC
                LIMIT 3;
                """).parse();
        assertEquals(1, query.sortColumns().size());
        assertFalse(query.sortColumns().get(0).ascending());
    }

    @Test
    void shouldParseSelectionTruncation() {
        Query query = new SqlSelectParser("select * from A limit 10 offset 5").parse();
        assertEquals(10, query.limit());
        assertEquals(5, query.offset());

        query = new SqlSelectParser("select * from A").parse();
        assertNull(query.limit());
        assertNull(query.offset());
    }

    @Test
    void shouldReturnSameSql() {
        String sqlOriginal = "SELECT author.name, count(book.id), sum(book.cost) " +
                "FROM author AS a " +
                "LEFT JOIN book ON (author.id = book.author_id) " +
                "GROUP BY author.name HAVING COUNT(*) > 1 AND SUM(book.cost) > 500 LIMIT 10;";
        Query query = new SqlSelectParser(sqlOriginal).parse();

        Query query2 = new SqlSelectParser(query.toSql()).parse();

        assertEquals(query.toSql(), query2.toSql());
    }
}
