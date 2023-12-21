package org.datawarehouse.module;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.datawarehouse.connection.DBConnection;
import org.datawarehouse.entity.DataFileConfigs;
import org.datawarehouse.entity.DataFiles;
import org.datawarehouse.entity.KQXS;
import org.datawarehouse.ultils.Configuration;
import org.datawarehouse.ultils.EmailSender;
import org.datawarehouse.ultils.Helper;
import org.datawarehouse.ultils.PropertiesReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class CrawlData {
    Helper helper = new Helper();

    public List<KQXS> resultMB(String sourcePath) throws Exception {
        List<KQXS> result = new ArrayList<>();
        String region = "Miền Bắc";

        String runDate = new PropertiesReader().getProperty("run.date");
        String dateInput = new PropertiesReader().getProperty("run.date.value");
        if (runDate.equals("on")) {
            Document document = Jsoup.connect(sourcePath + "xsmb/ngay-" + dateInput).get();
            Element el = document.getElementsByClass("result").get(0);
            Elements els = el.select("tbody").get(0).select("tr");
            String province = helper.extractStringInParentheses(els.get(0).text());
            String dateHtml = el.select("tr").get(0).select("th").get(0).select("i").get(0).attr("title");
            String date = helper.extractDate(dateHtml);
            for (int i = 1; i < els.size() - 1; i++) {
                String award = els.get(i).select("td").get(0).attr("title");

                if (i != 5 && i != 8) {
                    String numberHtml = els.get(i).select("td").get(1).text();
                    String[] numbers = helper.extractNumbers(numberHtml);
                    for (String number : numbers) {
                        result.add(new KQXS(region, province, award, number, date));
                    }
                }
            }
        } else if (runDate.equals("off")) {
            Document document = Jsoup.connect(sourcePath).get();
            Element el = document.getElementsByClass("result").get(0);
            Elements els = el.select("tbody").get(0).select("tr");
            String province = helper.extractStringInParentheses(els.get(0).text());
            String dateHtml = el.select("tr").get(0).select("th").get(0).select("i").get(0).attr("title");
            String date = helper.extractDate(dateHtml);
            for (int i = 1; i < els.size() - 1; i++) {
                String award = els.get(i).select("td").get(0).attr("title");

                if (i != 5 && i != 8) {
                    String numberHtml = els.get(i).select("td").get(1).text();
                    String[] numbers = helper.extractNumbers(numberHtml);
                    for (String number : numbers) {
                        result.add(new KQXS(region, province, award, number, date));
                    }
                }
            }
        }
        return result;
    }

    public List<KQXS> resultMTMN(String sourcePath) throws Exception {
        List<KQXS> result = new ArrayList<>();
        String[] regions = {"xsmt", "xsmn"};
        String runDate = new PropertiesReader().getProperty("run.date");
        String dateInput = new PropertiesReader().getProperty("run.date.value");
        if (runDate.equals("on")) {
            for (String region : regions) {
                Document document = Jsoup.connect(sourcePath + region + "/ngay-" + dateInput).get();
                Element el = document.getElementsByClass("box-ketqua").get(0);
                Elements els = el.select("tr").get(0).select("th");
                for (int i = 1; i < els.size(); i++) {
                    for (int j = 1; j < 10; j++) {
                        String province = els.get(i).text();
                        String awardNames = el.select("tr").get(j).select("td").get(0).attr("title");
                        String numbers = el.select("tr").get(j).select("td").get(i).text();
                        String[] extractNumbers = helper.extractNumbers(numbers);
                        for (String number : extractNumbers) {
                            result.add(new KQXS(helper.getNameRegion(region), province, awardNames, number, dateInput));
                        }
                    }
                }
            }
        } else if (runDate.equals("off")) {
            for (String region : regions) {
                Document document = Jsoup.connect(sourcePath + region).get();
                Element el = document.getElementsByClass("box-ketqua").get(0);
                Elements els = el.select("tr").get(0).select("th");
                String dateHtml = el.select("h2").get(0).select("a").get(1).attr("href");
                for (int i = 1; i < els.size(); i++) {
                    for (int j = 1; j < 10; j++) {
                        // date
                        String date = helper.extractDate(dateHtml);
                        String province = els.get(i).text();
                        String awardNames = el.select("tr").get(j).select("td").get(0).attr("title");
                        String numbers = el.select("tr").get(j).select("td").get(i).text();
                        String[] extractNumbers = helper.extractNumbers(numbers);
                        for (String number : extractNumbers) {
                            result.add(new KQXS(helper.getNameRegion(region), province, awardNames, number, date));
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<KQXS> getData(String sourcePath) throws Exception {
        List<KQXS> result = new ArrayList<>();

        List<KQXS> mb = resultMB(sourcePath);
        for (KQXS k : mb) {
            result.add(k);
        }

        List<KQXS> mtmn = resultMTMN(sourcePath);
        for (KQXS k : mtmn) {
            result.add(k);
        }

        return result;
    }

    // 9. Crawler data
    public void exportFileExcel(Connection connection, int configId, String location, String sourcePath, String fileName, String format) {
        try {
            String dateCrawl = helper.dateCrawl();
            // 9. Crawler data
            List<KQXS> kqxs = getData(sourcePath);
            String[] columnsTitle = {"Region", "Province", "Award", "Number", "Date"};
            // 10.2 Tìm kiếm file
            File file = new File(location + fileName + "_" + dateCrawl + format);
            // 10.2 Tồn tại ?
            if (!file.exists()) {
                // 10.4 Insert thêm dòng mới có status = EXTRACTING trong control.data_files
                new Configuration().insertStatusConfig(connection, configId, "CRAWLING", "DATA IS CRAWLING");
                Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(location + fileName + "_" + dateCrawl + format);
                Sheet sheet = workbook.createSheet("sheet1");

                // TAO TIEU DE
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columnsTitle.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columnsTitle[i]);
                }

                int rowNum = 1;
                for (KQXS kq : kqxs) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(kq.getRegion());
                    row.createCell(1).setCellValue(kq.getProvince());
                    row.createCell(2).setCellValue(kq.getAward());
                    row.createCell(3).setCellValue(kq.getNumber());
                    row.createCell(4).setCellValue(kq.getDate());
                }

                // EXPORT
                workbook.write(fos);
                System.out.println("Success");
                new Configuration().insertStatusConfig(connection, configId, "CRAWLED", "DATA IS CRAWLED");
            } else if (file.exists()) {
                System.out.println(file + " is exist!");
                file.delete();
                System.out.println("Delete file success");
                new Configuration().insertStatusConfig(connection, configId, "CRAWLING", "DATA IS CRAWLING");

                Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(location + fileName + "_" + dateCrawl + format);

                Sheet sheet = workbook.createSheet("sheet1");

                // TAO TIEU DE
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columnsTitle.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columnsTitle[i]);
                }

                int rowNum = 1;
                for (KQXS kq : kqxs) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(kq.getRegion());
                    row.createCell(1).setCellValue(kq.getProvince());
                    row.createCell(2).setCellValue(kq.getAward());
                    row.createCell(3).setCellValue(kq.getNumber());
                    row.createCell(4).setCellValue(kq.getDate());
                }

                // EXPORT
                workbook.write(fos);
                System.out.println("Export to file excel Success");
                new Configuration().insertStatusConfig(connection, configId, "CRAWLED", "DATA IS CRAWLED");
            }
        } catch (Exception e) {
            // 10.8 Insert thêm dòng mới có status = ERROR trong control.data_files
            new Configuration().insertStatusConfig(connection, configId, "ERROR", "PROCESSING ERROR IN CRAWL STEP");
            // 25. Gửi email báo cáo lỗi
            new EmailSender().sendEmail("ERROR IN CRAWLING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        // 1. Đọc file properties
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

            if (status != null) {
                String dateCrawlNewest = new Helper().extractDate(dataFile.getName());
                String dateCrawl = new Helper().dateCrawl();
                //
                if (!dateCrawl.equals(dateCrawlNewest)) {
                    if (status.equals("FINISH")) {
                        new CrawlData().exportFileExcel(connection, config.getId(), config.getLocation(),
                                config.getSource_path(), config.getFile_name(), config.getFormat());
                    } else
                        //
                        if (status.equals("CRAWLED")) {
                            configuration.insertStatusConfig(connection, config.getId(), "ERROR"
                                    , "The data has been CRAWLED");
                            emailSender.sendEmail("The data has been CRAWLED. Please proceed to the next step !!!.");
                        } else {
                            configuration.insertStatusConfig(connection, config.getId(), "ERROR"
                                    , "Processing error in crawl step");
                            emailSender.sendEmail("Processing error !!!.");
                        }
                } else
                    //
                    if (status.equals("CRAWLING")) {
                        new CrawlData().exportFileExcel(connection, config.getId(), config.getLocation(),
                                config.getSource_path(), config.getFile_name(), config.getFormat());
                    } else
                        //
                        if (status.equals("CRAWLED")) {
                            configuration.insertStatusConfig(connection, config.getId(), "ERROR"
                                    , "The data has been CRAWLED");
                            emailSender.sendEmail("The data has been CRAWLED. Please proceed to the next step !!!.");
                        } else {
                            configuration.insertStatusConfig(connection, config.getId(), "ERROR"
                                    , "Processing error in crawl step");
                            emailSender.sendEmail("Processing error !!!.");
                        }
            } else {
                new CrawlData().exportFileExcel(connection, config.getId(), config.getLocation(),
                        config.getSource_path(), config.getFile_name(), config.getFormat());
            }
        }
        dbConnection.closeConnection(connection);
    }

}
