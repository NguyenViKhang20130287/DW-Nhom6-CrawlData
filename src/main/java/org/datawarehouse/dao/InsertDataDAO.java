package org.datawarehouse.dao;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.datawarehouse.connection.DBConnection;
import org.datawarehouse.ultils.PropertiesReader;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InsertDataDAO {
    CrawlerDAO crawlerDAO = new CrawlerDAO();
    DBConnection dbConnection = new DBConnection();

    Connection connection;
    PreparedStatement ps = null;
    ResultSet rs;

    // 10. EXTRACT file excel vào staging
    public void insertDataToStaging(int configId, String USERNAME, String PASSWORD, String location, String fileName) {
        String DATABASE_NAME = new PropertiesReader().getProperty("database.name.staging");

        try {
            // 10.1 Kết nối database staging
            connection = dbConnection.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            // 10.2 Insert thêm dòng mới có status = EXTRACTING trong control.data_files
            new ConfigurationDAO().insertStatusConfig(configId, "EXTRACTING");
            // 10.3 Đọc file
            FileInputStream fis = new FileInputStream(location+fileName);

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
            // 10.4 Trancate table ketquaxoso_staging
            String queryTruncate = "truncate table ketquaxoso_staging";
            PreparedStatement psTruncate = connection.prepareStatement(queryTruncate);
            psTruncate.executeUpdate();
            psTruncate.close();
            System.out.println("Truncate data staging success");

            ps = connection.prepareStatement(query);

            // 10.5 Duyệt file
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
            // 10.6 insert dữ liệu vào staging.ketquaxoso_staging
            int[] rowQuantity = ps.executeBatch();
            System.out.println("Row quantity: " + rowQuantity.length);

            // CLOSE
            ps.close();
            System.out.println("Insert data to Staging success");
            // 26. Đóng kết nối database
            dbConnection.closeConnection(connection);

            // 10.6 Insert thêm dòng mới có status = EXTRACTED trong control.data_files
            new ConfigurationDAO().insertStatusConfig(configId, "EXTRACTED");

        } catch (Exception e) {
            // 10.8 Insert thêm dòng mới có status = ERROR trong control.data_files
            new ConfigurationDAO().insertStatusConfig(configId, "ERROR");
            // 25. Gửi email báo cáo lỗi
            new EmailSenderDAO().sendEmail("ERROR IN EXTRACTING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
