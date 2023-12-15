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

    // 11. Transform data
    public void transformData(int configId, String USERNAME, String PASSWORD) {
        try {
            // 11.1. Đọc file properties
            String DATABASE_NAME = new PropertiesReader().getProperty("database.name.staging");
            String[] nameFuncs = {"insertDataToMienDim", "insertDataToTinhDim", "insertDataToGiaiDim"};
            // 11.2 Kết nối database staging
            connection = dbConnection.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            // 11.3 Insert thêm dòng mới có status = TRANSFORMING trong control.data_files
            configurationDAO.insertStatusConfig(configId, "TRANSFORMING");
            for (String nameFunc : nameFuncs) {
                // 11.4 Insert các tên miền chưa có trong mien_dim vào mien_dim
                // 11.5 Insert các tên tỉnh chưa có trong tinh_dim vào tinh_dim
                // 11.6 Insert các tên giải chưa có trong giai_dim vào  giai_dim
                callableStatement = connection.prepareCall("{call " + nameFunc + "()}");
                callableStatement.execute();
                System.out.println("Run: " + nameFunc + " success");
            }

            // CLOSE
            callableStatement.close();
            // 26. Đóng kết nối control
            dbConnection.closeConnection(connection);
            // 11.7 Insert thêm dòng mới có status = TRANSFORMED trong control.data_files
            new ConfigurationDAO().insertStatusConfig(configId, "TRANSFORMED");

        } catch (Exception e) {
            // 11.8 Insert thêm dòng mới có status = ERROR trong control.data_files
            configurationDAO.insertStatusConfig(configId, "ERROR");
            // 25. Gửi email báo cáo lỗi
            new EmailSenderDAO().sendEmail("ERROR IN TRANSFORMING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 12. Thêm dữ liệu vào warehouse
    public void insertDataToFact(int configId, String USERNAME, String PASSWORD) {
        try {
            // 12.1 Đọc file properties
            String DATABASE_NAME = new PropertiesReader().getProperty("database.name.staging");
            String nameFunc = "insertDataToFact";
            // 12.2 Kết nối database staging
            connection = new DBConnection().getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            // 12.3 Insert thêm dòng mới có status = FACT-LOADING trong control.data_files
            configurationDAO.insertStatusConfig(configId, "FACT-LOADING");
            // 12.4 Join lần lượt các bảng warehouse.date_dim, warehouse.mien_dim, warehouse.tinh_dim,
            // warehouse.giai_dim với staging.ketquaxoso.staging
            // 12.5 Insert dữ liệu vào warehouse.ketquaxoso.fact
            CallableStatement callableStatement = connection.prepareCall("{call " + nameFunc + "()}");
            callableStatement.execute();

            System.out.println("Run: " + nameFunc + " success");
            // CLOSE
            callableStatement.close();
            // 26. Đóng kết nối control
            dbConnection.closeConnection(connection);
            // 12.6 Insert thêm dòng mới có status = FACT-LOADED trong control.data_files
            configurationDAO.insertStatusConfig(configId, "FACT-LOADED");
            System.out.println("Insert data to warehouse success");

        } catch (Exception e) {
            // 12.7 Insert thêm dòng mới có status = ERROR trong control.data_files
            configurationDAO.insertStatusConfig(configId, "ERROR");
            // 25. Gửi email báo cáo lỗi
            new EmailSenderDAO().sendEmail("ERROR IN FACT-LOADING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 13. Aggregate data
    public void insertDataToAggregate(int configId, String USERNAME, String PASSWORD) {
        try {
            // 13.1 Đọc file propertíe
            String DATABASE_NAME = new PropertiesReader().getProperty("database.name.warehouse");
            String nameFunc = "insertDataToAggregate";
            // 13.2 Kết nối database warehouse
            connection = new DBConnection().getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            // 13.3 Insert thêm dòng mới có status = AGGREGATE-LOADING trong control.data_files
            configurationDAO.insertStatusConfig(configId, "AGGREGATE-LOADING");
            // 13.4 Join lần lượt các bảng warehouse.date_dim, warehouse.mien_dim,
            // warehouse.tinh_dim, warehouse.giai_dim với warehouse.ketquaxoso.fact
            // 13.5 Insert dữ liệu vào warehouse.ketquaxoso.aggregate
            CallableStatement callableStatement = connection.prepareCall("{call " + nameFunc + "()}");
            callableStatement.execute();

            System.out.println("Run: " + nameFunc + " success");
            // CLOSE
            callableStatement.close();
            // 26. Đóng kết nối control
            dbConnection.closeConnection(connection);
            // 13.6 Insert thêm dòng mới có status = FACT-LOADED trong control.data_files
            configurationDAO.insertStatusConfig(configId, "AGGREGATE-LOADED");
            System.out.println("Insert data to aggregate success");

        } catch (Exception e) {
            // 13.7 Insert thêm dòng mới có status = ERROR trong control.data_files
            configurationDAO.insertStatusConfig(configId, "ERROR");
            // 25. Gửi email báo cáo lỗi
            new EmailSenderDAO().sendEmail("ERROR IN AGGREGATE-LOADING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 14. Thêm dữ liệu vào data_mart
    public void insertDataToMart(int configId, String USERNAME, String PASSWORD) {

        try {
            // 14.1 Kết nối database warehouse
            String DATABASE_NAME = new PropertiesReader().getProperty("database.name.warehouse");
            // 14.2 Kết nối database warehouse
            connection = new DBConnection().getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            // 14.3 Insert thêm dòng mới có status = MART-LOADING trong control.data_files
            configurationDAO.insertStatusConfig(configId, "MART-LOADING");
            String nameFunc = "insertDataToDataMart";
            // 14.4 Insert dữ liệu vào ketquaxoso_mart
            CallableStatement callableStatement = connection.prepareCall("{call " + nameFunc + "()}");
            callableStatement.execute();
            System.out.println("Run: " + nameFunc + " success");

            // CLOSE
            callableStatement.close();
            // 26. Đóng kết nối control
            dbConnection.closeConnection(connection);
            // 14.5 Insert thêm dòng mới có status = FINISH trong control.data_files
            configurationDAO.insertStatusConfig(configId, "FINISH");
            System.out.println("Insert data to Mart success");

        } catch (Exception e) {
            // 14.6 Insert thêm dòng mới có status = ERROR trong control.data_files
            configurationDAO.insertStatusConfig(configId, "ERROR");
            // 25. Gửi email báo cáo lỗi
            new EmailSenderDAO().sendEmail("ERROR IN MART-LOADING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
