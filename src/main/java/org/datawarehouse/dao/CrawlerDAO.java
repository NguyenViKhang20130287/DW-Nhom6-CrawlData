package org.datawarehouse.dao;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.datawarehouse.entity.KQXS;
import org.datawarehouse.ultils.PropertiesReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerDAO {

    public String getNameRegion(String pathName) {
        String region = "";
        if (pathName.equals("xsmn")) {
            region = "Miền Nam";
        } else if (pathName.equals("xsmt")) {
            region = "Miền Trung";
        } else if (pathName.equals("xsmb")) {
            region = "Miền Bắc";
        }
        return region;
    }

    public String extractStringInParentheses(String input) {
        int startIndex = input.indexOf("(");
        int endIndex = input.indexOf(")");

        // Kiểm tra xem có cặp dấu ngoặc đơn hay không
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return input.substring(startIndex + 1, endIndex);
        } else {
            return "Không tìm thấy chuỗi trong dấu ()";
        }
    }

    public String[] extractNumbers(String input) {
        // Sử dụng biểu thức chính quy để tìm các chuỗi số
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        // Tạo một danh sách để lưu trữ các chuỗi số được tìm thấy
        List<String> numberList = new ArrayList<>();

        // Duyệt qua các chuỗi số và thêm chúng vào danh sách
        while (matcher.find()) {
            numberList.add(matcher.group());
        }

        // Chuyển đổi danh sách thành mảng và trả về
        return numberList.toArray(new String[0]);
    }

    public String extractDate(String input) {
        // Sử dụng biểu thức chính quy để tìm kiếm ngày tháng năm
        Pattern pattern = Pattern.compile("\\b(\\d{1,2}-\\d{1,2}-\\d{4})\\b");
        Matcher matcher = pattern.matcher(input);

        // Nếu tìm thấy, trả về chuỗi ngày tháng năm
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public List<KQXS> resultMB(String sourcePath, String dateInput) throws Exception {
        List<KQXS> result = new ArrayList<>();
        String region = "Miền Bắc";

        String runDate = new PropertiesReader().getProperty("run.date");
        if (runDate.equals("on")) {
            Document document = Jsoup.connect(sourcePath + "/ngay-" + dateInput).get();
            Element el = document.getElementsByClass("result").get(0);
            Elements els = el.select("tbody").get(0).select("tr");
            String province = new CrawlerDAO().extractStringInParentheses(els.get(0).text());
            String dateHtml = el.select("tr").get(0).select("th").get(0).select("i").get(0).attr("title");
            String date = new CrawlerDAO().extractDate(dateHtml);
            System.out.println(date);
            System.out.println(province);
            for (int i = 1; i < els.size() - 1; i++) {
                String award = els.get(i).select("td").get(0).attr("title");

                if (i != 5 && i != 8) {
                    String numberHtml = els.get(i).select("td").get(1).text();
                    String[] numbers = new CrawlerDAO().extractNumbers(numberHtml);
                    for (String number : numbers) {
                        result.add(new KQXS(region, province, award, number, date));
                    }
                }
            }
        } else if (runDate.equals("off")) {
            Document document = Jsoup.connect(sourcePath).get();
            Element el = document.getElementsByClass("result").get(0);
            Elements els = el.select("tbody").get(0).select("tr");
            String province = new CrawlerDAO().extractStringInParentheses(els.get(0).text());
            String dateHtml = el.select("tr").get(0).select("th").get(0).select("i").get(0).attr("title");
            String date = new CrawlerDAO().extractDate(dateHtml);
            System.out.println(date);
            System.out.println(province);
            for (int i = 1; i < els.size() - 1; i++) {
                String award = els.get(i).select("td").get(0).attr("title");

                if (i != 5 && i != 8) {
                    String numberHtml = els.get(i).select("td").get(1).text();
                    String[] numbers = new CrawlerDAO().extractNumbers(numberHtml);
                    for (String number : numbers) {
                        result.add(new KQXS(region, province, award, number, date));
                    }
                }
            }
        }


        return result;
    }

    public List<KQXS> resultMTMN(String sourcePath, String dateInput) throws Exception {
        List<KQXS> result = new ArrayList<>();
        String[] regions = {"xsmt", "xsmn"};
        String runDate = new PropertiesReader().getProperty("run.date");
        if (runDate.equals("on")) {
            for (String region : regions) {
                Document document = Jsoup.connect(sourcePath + region + "/ngay-" + dateInput).get();
                Element el = document.getElementsByClass("box-ketqua").get(0);
                Elements els = el.select("tr").get(0).select("th");
                for (int i = 1; i < els.size(); i++) {
                    for (int j = 1; j < 10; j++) {
                        String province = els.get(i).text();
                        String awardNames = el.select("tr").get(j).select("td").get(0).attr("title");
                        String numbers = el.select("tr").get(j).select("td").get(1).text();
                        String[] extractNumbers = extractNumbers(numbers);
                        for (String number : extractNumbers) {
                            result.add(new KQXS(getNameRegion(region), province, awardNames, number, dateInput));
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
                System.out.println(dateHtml);
                for (int i = 1; i < els.size(); i++) {
                    for (int j = 1; j < 10; j++) {
                        // date
                        String date = extractDate(dateHtml);
                        String province = els.get(i).text();
                        String awardNames = el.select("tr").get(j).select("td").get(0).attr("title");
                        String numbers = el.select("tr").get(j).select("td").get(1).text();
                        String[] extractNumbers = extractNumbers(numbers);
                        for (String number : extractNumbers) {
                            result.add(new KQXS(getNameRegion(region), province, awardNames, number, date));
                        }
                    }
                }
            }
        }

        return result;
    }

    public List<KQXS> getData(String sourcePath, String date) throws Exception {
        List<KQXS> result = new ArrayList<>();
        String[] regions = {"xsmb", "xsmt", "xsmn"};

        for (String region : regions) {
            if (region.equals("xsmb")) {
                List<KQXS> mb = resultMB(sourcePath + region, date);
                for (KQXS k : mb) {
                    result.add(k);
                }
            } else {
                List<KQXS> mtmn = resultMTMN(sourcePath, date);
                for (KQXS k : mtmn) {
                    result.add(k);
                }
            }
        }
        return result;
    }

    public void exportFileExcel(int configId, String location, String sourcePath, String fileName, String date) {

        try {
            List<KQXS> kqxs = getData(sourcePath, date);
            String[] columnsTitle = {"Region", "Province", "Award", "Number", "Date"};
            File file = new File(location + fileName);
            if (!file.exists()) {
                new ConfigurationDAO().insertStatusConfig(configId, "CRAWLING");
                Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(location + fileName);
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
                new ConfigurationDAO().insertStatusConfig(configId, "CRAWLED");
            } else if (file.exists()) {
                System.out.println(file + " is exist!");
                file.delete();
                System.out.println("Delete file success");
                new ConfigurationDAO().insertStatusConfig(configId, "CRAWLING");

                Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(location + fileName);

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
                new ConfigurationDAO().insertStatusConfig(configId, "CRAWLED");
            }
        } catch (Exception e) {
            new ConfigurationDAO().insertStatusConfig(configId, "ERROR");
            new EmailSenderDAO().sendEmail("ERROR IN CRAWLING STEP: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
