package org.datawarehouse.dao;

import org.datawarehouse.connection.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;

public class RunProcedureDAO {

    Connection connection;
    DBConnection dbConnection = new DBConnection();
    ConfigurationDAO configurationDAO = new ConfigurationDAO();
    CallableStatement callableStatement;
    public void transformData(int configId, String USERNAME, String PASSWORD) {
        configurationDAO.insertStatusConfig(configId, "TRANSFORMING");
        String DATABASE_NAME = "staging";
        String[] nameFuncs = {"ThemDuLieuMienDim", "ThemDuLieuTinhDim", "ThemDuLieuGiaiDim", "ThemDuLieuKetQuaXoSoFact"};
        try {
            connection = dbConnection.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            for (String nameFunc : nameFuncs) {
                callableStatement = connection.prepareCall("{call " + nameFunc + "()}");

                callableStatement.execute();
                System.out.println("Run: " + nameFunc + " success");
            }

            // CLOSE
            callableStatement.close();
            dbConnection.closeConnection(connection);
            new ConfigurationDAO().insertStatusConfig(configId, "TRANSFORMED");
        } catch (Exception e) {
            e.printStackTrace();
            configurationDAO.insertStatusConfig(configId, "ERROR");
            new EmailSenderDAO().sendEmail(e.getMessage());
        }
    }

    public void insertDataToMart(int configId, String USERNAME, String PASSWORD) {
        configurationDAO.insertStatusConfig(configId, "MART-LOADING");
        String DATABASE_NAME = "warehouse";
        String nameFunc = "InsertIntoKetQuaXoSoMart";
        try {
            connection = new DBConnection().getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            CallableStatement callableStatement = connection.prepareCall("{call " + nameFunc + "()}");

            callableStatement.execute();
            System.out.println("Run: " + nameFunc + " success");

            // CLOSE
            callableStatement.close();
            dbConnection.closeConnection(connection);
            configurationDAO.insertStatusConfig(configId, "MART-LOADED");
            System.out.println("Insert data to Mart success");
        } catch (Exception e) {
            e.printStackTrace();
            configurationDAO.insertStatusConfig(configId, "ERROR");
            new EmailSenderDAO().sendEmail(e.getMessage());
        }
    }
}
