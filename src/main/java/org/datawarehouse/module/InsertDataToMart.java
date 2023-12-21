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

public class InsertDataToMart {
    Connection connection;
    DBConnection dbConnection = new DBConnection();
    Configuration configuration = new Configuration();
    CallableStatement callableStatement;

    public void start(Connection conn, int configId, String USERNAME, String PASSWORD) {

        try {
            // 14.1 Kết nối database warehouse
            String DATABASE_NAME = new PropertiesReader().getProperty("database.name.warehouse");
            // 14.2 Kết nối database warehouse
            connection = new DBConnection().getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            // 14.3 Insert thêm dòng mới có status = MART-LOADING trong control.data_files
            configuration.insertStatusConfig(conn, configId, "MART-LOADING", "DATA IS LOADING TO DATA_MART");
            String nameFunc = "insertDataToDataMart";
            // 14.4 Insert dữ liệu vào ketquaxoso_mart
            callableStatement = connection.prepareCall("{call " + nameFunc + "()}");
            callableStatement.execute();
            System.out.println("Run: " + nameFunc + " success");

            // CLOSE
            callableStatement.close();
            // 26. Đóng kết nối control
            dbConnection.closeConnection(connection);
            // 14.5 Insert thêm dòng mới có status = FINISH trong control.data_files
            configuration.insertStatusConfig(conn, configId, "FINISH", "DATA IS LOADED TO DATA_MART");
            System.out.println("Insert data to Mart success");

        } catch (Exception e) {
            // 14.6 Insert thêm dòng mới có status = ERROR trong control.data_files
            configuration.insertStatusConfig(configId, "ERROR", "PROCESSING ERROR IN LOAD TO DATA_MART STEP");
            // 25. Gửi email báo cáo lỗi
            new EmailSender().sendEmail("ERROR IN MART-LOADING STEP: " + e.getMessage());
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
                if (status.equals("AGGREGATE-LOADED") || status.equals("MART-LOADING")) {
                    new InsertDataToMart().start(connection, config.getId(), config.getUsername(), config.getPassword());
                }else if (status.equals("FINISH")){
                    configuration.insertStatusConfig(connection, config.getId(), "ERROR"
                            , "The data has been AGGREGATE-LOADED");
                    emailSender.sendEmail("The data has been AGGREGATE-LOADED. Please proceed to the next step.");
                } else {
                    configuration.insertStatusConfig(connection, config.getId(), "ERROR"
                            , "Processing error in load to mart step");
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