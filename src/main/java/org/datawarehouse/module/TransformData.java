package org.datawarehouse.module;

import org.datawarehouse.connection.DBConnection;
import org.datawarehouse.entity.DataFileConfigs;
import org.datawarehouse.entity.DataFiles;
import org.datawarehouse.ultils.Configuration;
import org.datawarehouse.ultils.EmailSender;
import org.datawarehouse.ultils.Helper;
import org.datawarehouse.ultils.PropertiesReader;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;

public class TransformData {
    Connection connection;
    DBConnection dbConnection = new DBConnection();
    Configuration configuration = new Configuration();
    CallableStatement callableStatement;

    // 11. Transform data
    public void start(Connection conn, int configId, String USERNAME, String PASSWORD) {
        try {
            // 11.1. Đọc file properties
            String DATABASE_NAME = new PropertiesReader().getProperty("database.name.staging");
            String[] nameFuncs = {"insertDataToMienDim", "insertDataToTinhDim", "insertDataToGiaiDim"};
            // 11.2 Kết nối database staging
            connection = dbConnection.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            // 11.3 Insert thêm dòng mới có status = TRANSFORMING trong control.data_files
            configuration.insertStatusConfig(conn, configId, "TRANSFORMING", "DATA IS TRANSFORMING");
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
            new Configuration().insertStatusConfig(conn, configId, "TRANSFORMED", "DATA IS TRANSFORMED");

        } catch (Exception e) {
            // 11.8 Insert thêm dòng mới có status = ERROR trong control.data_files
            configuration.insertStatusConfig(conn, configId, "ERROR", "PROCESSING ERROR IN TRANSFORM STEP");
            // 25. Gửi email báo cáo lỗi
            new EmailSender().sendEmail("ERROR IN TRANSFORMING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        PropertiesReader pr = new PropertiesReader();
        String DATABASE_NAME = pr.getProperty("database.name.control");
        String USERNAME = pr.getProperty("database.username");
        String PASSWORD = pr.getProperty("database.password");
        Configuration configuration = new Configuration();
        EmailSender emailSender = new EmailSender();
        //
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
        List<DataFileConfigs> configs = configuration.getDataConfigWithFlag(connection);
        for (DataFileConfigs config : configs) {
            DataFiles dataFile = configuration.getDataFilesWithFileTimeStampNewestNoErr(connection, config.getId());
            String status = dataFile.getStatus();

            String dateCrawlNewest = new Helper().extractDate(dataFile.getName());
            String dateCrawl = new Helper().dateCrawl();
            if (dateCrawl.equals(dateCrawlNewest)) {
                if (status.equals("EXTRACTED") || status.equals("TRANSFORMING")) {
                    new TransformData().start(connection, config.getId(), config.getUsername(), config.getPassword());
                }else if (status.equals("TRANSFORMED")){
                    configuration.insertStatusConfig(connection, config.getId(), "ERROR"
                            , "The data has been TRANSFORMED");
                    emailSender.sendEmail("The data has been TRANSFORMED. Please proceed to the next step.");
                } else {
                    configuration.insertStatusConfig(connection, config.getId(), "ERROR"
                            , "Processing error in transform step");
                    emailSender.sendEmail("Processing error !!!.");
                }
            } else {
                configuration.insertStatusConfig(connection, config.getId(), "ERROR"
                        , "The previous process has not been completed");
                emailSender.sendEmail("The previous process has not been completed !!!.");
            }
        }
        dbConnection.closeConnection(connection);
    }

}
