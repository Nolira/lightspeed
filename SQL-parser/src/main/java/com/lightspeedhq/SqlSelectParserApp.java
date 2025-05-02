package com.lightspeedhq;

import com.lightspeedhq.model.Query;
import com.lightspeedhq.util.SqlSelectParser;

public class SqlSelectParserApp {
    public static void main(String[] args) {
        Query query = new SqlSelectParser("""
                SELECT author.name, count(book.id), sum(book.cost) 
                FROM author 
                LEFT JOIN book ON (author.id = book.author_id) 
                GROUP BY author.name 
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                LIMIT 10
                """).parse();
        System.out.println(query + "\n");
        System.out.println(query.toSql() + "\n");

        query = new SqlSelectParser("select * from (select * from A) as A where (A.id = 1 or (A.id > 100 and A.id < 1000))").parse();
        System.out.println(query + "\n");
        System.out.println(query.toSql() + "\n");

        query = new SqlSelectParser("""
                SELECT 
                    id AS order_id, 
                    customer_id, 
                    order_date 
                FROM orders 
                WHERE (order_date > TIMESTAMP('2025-01-01') or order_date < TIMESTAMP('2025-04-01'));
                """).parse();
        System.out.println(query + "\n");
        System.out.println(query.toSql() + "\n");

        query = new SqlSelectParser("""
                SELECT 
                    *
                FROM people 
                WHERE people.name="John";
                """).parse();
        System.out.println(query + "\n");
        System.out.println(query.toSql() + "\n");
    }
}
