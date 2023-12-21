package org.datawarehouse.connection;

import org.datawarehouse.ultils.PropertiesReader;

import java.sql.*;

public class DBConnection {

    PropertiesReader pr = new PropertiesReader();

    public Connection getConnection(String dbName, String username, String password) throws Exception {
        String dbURL = pr.getProperty("database.url") + dbName + "?useUnicode=yes&characterEncoding=UTF-8";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(dbURL, username, password);
        System.out.println(">>>>>>> " + "connect " + dbName.toUpperCase() + " successfully!");
        return conn;
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(">>>>>>> " + "Connection closed successfully!");
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
//        Connection connection = new DBConnection().getConnection("control", "root", "");
//        System.out.println(new DBConnection().getDataConfigWithFlag(connection));
//        new DBConnection().closeConnection(connection);
    }
}
