package org.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public Connection getConnection(String dbName) {
        String dbURL = "jdbc:mysql://localhost:3306/" + dbName + "?useUnicode=yes&characterEncoding=UTF-8";
        String userName = "root";
        String password = "";
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, userName, password);
            System.out.println("connect successfully!");
        } catch (Exception ex) {
            System.out.println("connect failure!");
            ex.printStackTrace();
        }
        return conn;
    }
}
