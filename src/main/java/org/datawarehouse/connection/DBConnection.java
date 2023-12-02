package org.datawarehouse.connection;

import org.datawarehouse.dao.ConfigurationDAO;
import org.datawarehouse.dao.EmailSenderDAO;
import org.datawarehouse.ultils.PropertiesReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    PropertiesReader pr = new PropertiesReader();

    public Connection getConnection(String dbName, String username, String password) {
        String dbURL = pr.getProperty("database.url") + dbName + "?useUnicode=yes&characterEncoding=UTF-8";
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, username, password);
            System.out.println("connect successfully!");
        } catch (Exception ex) {
            System.out.println("connect failure!");
            ex.printStackTrace();
            new EmailSenderDAO().sendEmail(ex.getMessage());
        }
        return conn;
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Status isClosed: " + connection.isClosed());
                System.out.println("Connection closed successfully!");
                System.out.println("------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
