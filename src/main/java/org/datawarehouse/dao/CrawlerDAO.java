package org.datawarehouse.dao;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.datawarehouse.entity.KQXS;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerDAO {

    public String getNameRegion(String pathName){
        String region = "";
        if(pathName.equals("xsmn")){
            region = "Miền Nam";
        } else if(pathName.equals("xsmt")){
            region = "Miền Trung";
        } else if(pathName.equals("xsmb")){
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

    public List<KQXS> resultMB(String sourcePath) {
        List<KQXS> result = new ArrayList<>();
        String region = "Miền Bắc";
        try {
            Document document = Jsoup.connect(sourcePath).get();
            Element el = document.getElementsByClass("result").get(0);
            //province
            String provinceHtml = el.select("tr").get(0).select("th").get(0).select("b").get(0).text();
            String province = extractStringInParentheses(provinceHtml);

            // date
            String dateHtml = el.select("tr").get(0).select("th").get(0).select("i").get(0).attr("title");
            String date = extractDate(dateHtml);

            String DB_name = el.select("tr").get(1).select("td").get(0).attr("title");
            String DB_number = el.select("tr").get(1).select("td").get(1).text();
            result.add(new KQXS(region, province, DB_name, DB_number, date));

            String G1_name = el.select("tr").get(2).select("td").get(0).attr("title");
            String G1_number = el.select("tr").get(2).select("td").get(1).text();
            result.add(new KQXS(region, province, G1_name, G1_number, date));

            String G2_name = el.select("tr").get(3).select("td").get(0).attr("title");
            String G2_number = el.select("tr").get(3).select("td").get(1).text();
            String[] G2_numbers = extractNumbers(G2_number);
            for (String g : G2_numbers) {
                result.add(new KQXS(region, province, G2_name, g, date));
            }

            String G3_name = el.select("tr").get(4).select("td").get(0).attr("title");
            String G3_number = el.select("tr").get(4).select("td").get(1).text();
            String[] G3_numbers = extractNumbers(G3_number);
            for (String g : G3_numbers) {
                result.add(new KQXS(region, province, G3_name, g, date));
            }

            String G4_name = el.select("tr").get(6).select("td").get(0).attr("title");
            String G4_number = el.select("tr").get(6).select("td").get(1).text();
            String[] G4_numbers = extractNumbers(G4_number);
            for (String g : G4_numbers) {
                result.add(new KQXS(region, province, G4_name, g, date));
            }

            String G5_name = el.select("tr").get(7).select("td").get(0).attr("title");
            String G5_number = el.select("tr").get(7).select("td").get(1).text();
            String[] G5_numbers = extractNumbers(G5_number);
            for (String g : G5_numbers) {
                result.add(new KQXS(region, province, G5_name, g, date));
            }

            String G6_name = el.select("tr").get(9).select("td").get(0).attr("title");
            String G6_number = el.select("tr").get(9).select("td").get(1).text();
            String[] G6_numbers = extractNumbers(G6_number);
            for (String g : G6_numbers) {
                result.add(new KQXS(region, province, G6_name, g, date));
            }

            String G7_name = el.select("tr").get(10).select("td").get(0).attr("title");
            String G7_number = el.select("tr").get(10).select("td").get(1).text();
            String[] G7_numbers = extractNumbers(G7_number);
            for (String g : G7_numbers) {
                result.add(new KQXS(region, province, G7_name, g, date));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public List<KQXS> resultMTMN(String sourcePath) {
        List<KQXS> result = new ArrayList<>();
        String[] regions = {"xsmt", "xsmn"};
        for (String region : regions) {
            try {
                Document document = Jsoup.connect(sourcePath + region).get();
                Element el = document.getElementsByClass("box-ketqua").get(0);
                Elements els = el.select("tr").get(0).select("th");
                String dateHtml = el.select("h2").get(0).select("a").get(1).attr("href");
                for (int i = 1; i < els.size(); i++) {
                    for (int j = 1; j < 10; j++) {


                        // date
                        String date = extractDate(dateHtml);

                        String province = els.get(i).text();
                        String awardNames = el.select("tr").get(j).select("td").get(0).attr("title");
                        String numbers = el.select("tr").get(j).select("td").get(1).text();
                        String[] extractNumbers = extractNumbers(numbers);
                        for(String number : extractNumbers){
                            result.add(new KQXS(getNameRegion(region), province, awardNames, number, date));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public List<KQXS> getData(String sourcePath) {
        List<KQXS> result = new ArrayList<>();
        String[] regions = {"xsmb", "xsmt", "xsmn"};

        try {
            for (String region : regions) {
                if (region.equals("xsmb")) {
                    List<KQXS> mb = resultMB(sourcePath+region);
                    for (KQXS k : mb) {
                        result.add(k);
                    }

                } else {
                        List<KQXS> mtmn = resultMTMN(sourcePath);
                        for (KQXS k : mtmn) {
                            result.add(k);
                        }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void exportFileExcel(int configId, String location, String sourcePath) {

        List<KQXS> kqxs = getData(sourcePath);
        String[] columnsTitle = {"Region", "Province", "Award", "Number", "Date"};

        try {
            File file = new File(location);
            if (!file.exists()) {
                new ConfigurationDAO().insertStatusConfig(configId, "CRAWLING");
                Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(location);
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
            } else if(file.exists()) {
                System.out.println(file + " is exist!");
                file.delete();
                System.out.println("Delete file success");
                new ConfigurationDAO().insertStatusConfig(configId, "CRAWLING");

                Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(location);

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


        } catch (IOException e) {
            new ConfigurationDAO().insertStatusConfig(configId, "ERROR");
            new EmailSenderDAO().sendEmail(e.getMessage());
        }
    }


}
