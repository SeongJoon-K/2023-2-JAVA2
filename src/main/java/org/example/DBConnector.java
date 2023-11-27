package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/dguBook?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "tjdwns2!";

    public static Connection connect() {
        try {
            // MySQL JDBC 드라이버를 로드합니다.
            Class.forName("com.mysql.jdbc.Driver");
            // 데이터베이스에 연결을 시도합니다.
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the MySQL database", e);
        }
    }
}
