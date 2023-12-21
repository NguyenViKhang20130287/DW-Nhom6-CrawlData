package org.datawarehouse.module;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.datawarehouse.connection.DBConnection;
import org.datawarehouse.entity.DataFileConfigs;
import org.datawarehouse.entity.DataFiles;
import org.datawarehouse.ultils.Configuration;
import org.datawarehouse.ultils.EmailSender;
import org.datawarehouse.ultils.Helper;
import org.datawarehouse.ultils.PropertiesReader;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ExtractData {
    CrawlData crawlData = new CrawlData();
    DBConnection dbConnection = new DBConnection();

    Connection connection;
    PreparedStatement ps = null;
    ResultSet rs;

    // 10. EXTRACT file excel vào staging
    public void start(Connection conn, int configId, String USERNAME, String PASSWORD, String location,
                      String fileName, String format) {
        String DATABASE_NAME = new PropertiesReader().getProperty("database.name.staging");

        try {
            // 9.1  Kết nối database staging
            connection = dbConnection.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            // 9. 2 Insert thêm hàng mới với status = EXTRACTING vào control.data_files
            new Configuration().insertStatusConfig(conn, configId, "EXTRACTING", "DATA IS EXTRACTING");
            // 9.3 Đọc file KQXS_dd-mm-yyyy.xlsx
            FileInputStream fis = new FileInputStream(location + fileName + "_" + new Helper().dateCrawl() + format);

            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            String query = "INSERT INTO staging.ketquaxoso_staging ( ketquaxoso_staging.ngayXo, \n" +
                    "ketquaxoso_staging.mien, \n" +
                    "ketquaxoso_staging.tinh, \n" +
                    "ketquaxoso_staging.giai, \n" +
                    "ketquaxoso_staging.soTrungThuong )\n" +
                    "VALUES(" +
                    "STR_TO_DATE(?, '%d-%m-%Y')," +
                    "?, ?, ?, ?)";

            // TRUNCATE DATA IN DATABASE STAGING
            // 9.4 Truncate table staging.ketquaxoso_staging
            String queryTruncate = "truncate table ketquaxoso_staging";
            PreparedStatement psTruncate = connection.prepareStatement(queryTruncate);
            psTruncate.executeUpdate();
            psTruncate.close();
            System.out.println("Truncate data staging success");

            ps = connection.prepareStatement(query);

            // 9.5 Duyệt qua từng hàng, từng cột trong file KQXS_dd-mm-yyyy.xlsx
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String region = row.getCell(0).getStringCellValue();
                    String province = row.getCell(1).getStringCellValue();
                    String award = row.getCell(2).getStringCellValue();
                    String number = row.getCell(3).getStringCellValue();
                    String date = row.getCell(4).getStringCellValue();

                    //
                    ps.setString(1, date);
                    ps.setString(2, region);
                    ps.setString(3, province);
                    ps.setString(4, award);
                    ps.setString(5, number);
                    ps.addBatch();
                }
            }
            // 9.6 Insert dữ liệu vừa được duyệt vào staging.ketquaxoso_staging
            int[] rowQuantity = ps.executeBatch();
            System.out.println("Row quantity: " + rowQuantity.length);

            // CLOSE
            ps.close();
            System.out.println("Insert data to Staging success");
            // 9.7 Đóng kết nói database staging
            dbConnection.closeConnection(connection);

            // 9.8 Insert thêm hàng mới với status = EXTRACTED vào control.data_files
            new Configuration().insertStatusConfig(conn, configId, "EXTRACTED", "DATA IS EXTRACTED");

        } catch (Exception e) {
            // 10.8 Insert thêm dòng mới có status = ERROR trong control.data_files
            new Configuration().insertStatusConfig(conn, configId, "ERROR", "PROCESSING ERROR IN EXTRACT STEP");
            // 25. Gửi email báo cáo lỗi
            new EmailSender().sendEmail("ERROR IN EXTRACTING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        // 1. Đọc file properties
        PropertiesReader pr = new PropertiesReader();
        String DATABASE_NAME = pr.getProperty("database.name.control");
        String USERNAME = pr.getProperty("database.username");
        String PASSWORD = pr.getProperty("database.password");
        EmailSender emailSender = new EmailSender();
        // 2. Kết nối database control
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
        // 3. Lấy ra list data_file_configs có flag = 1
        List<DataFileConfigs> configs = new Configuration().getDataConfigWithFlag(connection);
        // 4. Lấy từng dòng join với control.data_files
        for (DataFileConfigs config : configs) {
            // 5. Lấy ra status và name của hàng có file_timeStamp mới nhất
            DataFiles dataFile = new Configuration().getDataFilesWithFileTimeStampNewestNoErr(connection, config.getId());
            String status = dataFile.getStatus();
            String dateCrawlNewest = new Helper().extractDate(dataFile.getName());
            String dateCrawl = new Helper().dateCrawl();
            // 6. So sánh ngày đang thực hiện xử lí với ngày đã thực hiện xử lí trước đó trong name của hàng vừa lấy ra
            if (dateCrawl.equals(dateCrawlNewest)) {
                // 7. Kiểm tra status
                // 8. status = CRAWLED ?
                // 8. status = EXTRACTING ?
                if (status.equals("CRAWLED") || status.equals("EXTRACTING")) {
                    // 9. Tiến hành thực hiện extract data
                    new ExtractData().start(connection, config.getId(), config.getUsername(), config.getPassword()
                            , config.getLocation(), config.getFile_name(), config.getFormat());
                } else
                    //
                    if (status.equals("EXTRACTED")) {
                        // 11. Insert thêm hàng mới với status = ERROR vào control.data_files
                        new Configuration().insertStatusConfig(connection, config.getId(), "ERROR"
                                , "The data has been EXTRACTED");
                        // 12. Gửi email thông báo
                        emailSender.sendEmail("The data has been EXTRACTED. Please proceed to the next step !!!.");
                    } else {
                        // 11. Insert thêm hàng mới với status = ERROR vào control.data_files
                        new Configuration().insertStatusConfig(connection, config.getId(), "ERROR"
                                , "Processing error in extract step");
                        // 12. Gửi email thông báo
                        emailSender.sendEmail("Processing error !!!.");
                    }
            } else {
                // 11. Insert thêm hàng mới với status = ERROR vào control.data_files
                new Configuration().insertStatusConfig(connection, config.getId(), "ERROR"
                        , "The previous process has not been completed");
                // 12. Gửi email thông báo
                emailSender.sendEmail("The previous process has not been completed !!!.");
            }
        }
        // 10. Đóng kết nối database control
        dbConnection.closeConnection(connection);
    }
}
