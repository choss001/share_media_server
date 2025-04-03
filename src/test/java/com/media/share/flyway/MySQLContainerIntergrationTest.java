//package com.media.share.flyway;
//
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.testcontainers.context.ImportTestcontainers;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Testcontainers
//public class MySQLContainerIntergrationTest {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Test
//    void testDatabaseConnection() throws SQLException{
//        try(Connection connection = dataSource.getConnection()){
//            assertThat(connection.isClosed()).isFalse();
//
//        }
//    }
//
//    @Test
//    void testInsertAndQuery(){
//        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test (id INT PRIMARY KEY, name VARCHAR(50))");
//        jdbcTemplate.execute("INSERT INTO test (id, name) VALUES (1, 'Sample Data')");
//
//        String name = jdbcTemplate.queryForObject("SELECT name FROM test WHERE id = 1", String.class);
//        assertThat(name).isEqualTo("Sample Data");
//    }
//
//}
