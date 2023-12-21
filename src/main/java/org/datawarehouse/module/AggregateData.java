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

public class AggregateData {
    Connection connection;
    DBConnection dbConnection = new DBConnection();
    Configuration configuration = new Configuration();
    CallableStatement callableStatement;

    public void start(Connection conn, int configId, String USERNAME, String PASSWORD) {
        try {
            // 13.1 Đọc file propertíe
            String DATABASE_NAME = new PropertiesReader().getProperty("database.name.warehouse");
            String nameFunc = "insertDataToAggregate";
            // 13.2 Kết nối database warehouse
            connection = new DBConnection().getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            // 13.3 Insert thêm dòng mới có status = AGGREGATE-LOADING trong control.data_files
            configuration.insertStatusConfig(conn, configId, "AGGREGATE-LOADING", "DATA IS AGGREGATING");
            // 13.4 Join lần lượt các bảng warehouse.date_dim, warehouse.mien_dim,
            // warehouse.tinh_dim, warehouse.giai_dim với warehouse.ketquaxoso.fact
            // 13.5 Insert dữ liệu vào warehouse.ketquaxoso.aggregate
            callableStatement = connection.prepareCall("{call " + nameFunc + "()}");
            callableStatement.execute();

            System.out.println("Run: " + nameFunc + " success");
            // CLOSE
            callableStatement.close();
            // 26. Đóng kết nối control
            dbConnection.closeConnection(connection);
            // 13.6 Insert thêm dòng mới có status = FACT-LOADED trong control.data_files
            configuration.insertStatusConfig(conn, configId, "AGGREGATE-LOADED", "DATA IS AGGREGATED");
            System.out.println("Insert data to aggregate success");

        } catch (Exception e) {
            // 13.7 Insert thêm dòng mới có status = ERROR trong control.data_files
            configuration.insertStatusConfig(conn, configId, "ERROR", "PROCESSING ERROR IN AGGREGATE STEP");
            // 25. Gửi email báo cáo lỗi
            new EmailSender().sendEmail("ERROR IN AGGREGATE-LOADING STEP: " + e.getMessage());
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
                if (status.equals("FACT-LOADED") || status.equals("AGGREGATE-LOADING")) {
                    new AggregateData().start(connection, config.getId(), config.getUsername(), config.getPassword());
                } else if (status.equals("AGGREGATE-LOADED")) {
                    configuration.insertStatusConfig(connection, config.getId(), "ERROR"
                            , "The data has been AGGREGATE-LOADED");
                    emailSender.sendEmail("The data has been AGGREGATE-LOADED. Please proceed to the next step.");
                } else {
                    configuration.insertStatusConfig(connection, config.getId(), "ERROR"
                            , "Processing error in aggregate step");
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
