package org.datawarehouse.dao;

import org.datawarehouse.connection.DBConnection;
import org.datawarehouse.ultils.PropertiesReader;

import java.sql.CallableStatement;
import java.sql.Connection;

public class RunProcedureDAO {
    Connection connection;
    DBConnection dbConnection = new DBConnection();
    ConfigurationDAO configurationDAO = new ConfigurationDAO();
    CallableStatement callableStatement;

    public void transformData(int configId, String USERNAME, String PASSWORD) {
        try {
            String DATABASE_NAME = new PropertiesReader().getProperty("database.name.staging");
            String[] nameFuncs = {"insertDataToMienDim", "insertDataToTinhDim", "insertDataToGiaiDim"};
            //
            connection = dbConnection.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            configurationDAO.insertStatusConfig(configId, "TRANSFORMING");
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
            configurationDAO.insertStatusConfig(configId, "ERROR");
            new EmailSenderDAO().sendEmail("ERROR IN TRANSFORMING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void insertDataToFact(int configId, String USERNAME, String PASSWORD) {
        try {
            String DATABASE_NAME = new PropertiesReader().getProperty("database.name.staging");
            String nameFunc = "insertDataToFact";
            connection = new DBConnection().getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            configurationDAO.insertStatusConfig(configId, "FACT-LOADING");
            CallableStatement callableStatement = connection.prepareCall("{call " + nameFunc + "()}");

            callableStatement.execute();
            System.out.println("Run: " + nameFunc + " success");

            // CLOSE
            callableStatement.close();
            dbConnection.closeConnection(connection);
            configurationDAO.insertStatusConfig(configId, "FACT-LOADED");
            System.out.println("Insert data to warehouse success");

        } catch (Exception e) {
            configurationDAO.insertStatusConfig(configId, "ERROR");
            new EmailSenderDAO().sendEmail("ERROR IN FACT-LOADING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void insertDataToAggregate(int configId, String USERNAME, String PASSWORD) {
        try {
            String DATABASE_NAME = new PropertiesReader().getProperty("database.name.warehouse");
            String nameFunc = "insertDataToAggregate";
            connection = new DBConnection().getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            configurationDAO.insertStatusConfig(configId, "AGGREGATE-LOADING");
            CallableStatement callableStatement = connection.prepareCall("{call " + nameFunc + "()}");

            callableStatement.execute();
            System.out.println("Run: " + nameFunc + " success");

            // CLOSE
            callableStatement.close();
            dbConnection.closeConnection(connection);
            configurationDAO.insertStatusConfig(configId, "AGGREGATE-LOADED");
            System.out.println("Insert data to aggregate success");

        } catch (Exception e) {
            configurationDAO.insertStatusConfig(configId, "ERROR");
            new EmailSenderDAO().sendEmail("ERROR IN AGGREGATE-LOADING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void insertDataToMart(int configId, String USERNAME, String PASSWORD) {

        try {
                configurationDAO.insertStatusConfig(configId, "MART-LOADING");
                String DATABASE_NAME = new PropertiesReader().getProperty("database.name.warehouse");
                String nameFunc = "insertDataToDataMart";
                connection = new DBConnection().getConnection(DATABASE_NAME, USERNAME, PASSWORD);
                CallableStatement callableStatement = connection.prepareCall("{call " + nameFunc + "()}");

                callableStatement.execute();
                System.out.println("Run: " + nameFunc + " success");

                // CLOSE
                callableStatement.close();
                dbConnection.closeConnection(connection);
                configurationDAO.insertStatusConfig(configId, "FINISH");
                System.out.println("Insert data to Mart success");

        } catch (Exception e) {
            configurationDAO.insertStatusConfig(configId, "ERROR");
            new EmailSenderDAO().sendEmail("ERROR IN MART-LOADING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
