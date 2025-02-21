package org.example.config;

import java.sql.*;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/socket_bbq";  // MySQL 서버 주소 및 데이터베이스 이름
    private static final String USER = "root";  // MySQL 사용자명
    private static final String PASSWORD = "hanbit";  // MySQL 비밀번호

    public static Connection connect() {
        try {
            // MySQL JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 데이터베이스 연결
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
