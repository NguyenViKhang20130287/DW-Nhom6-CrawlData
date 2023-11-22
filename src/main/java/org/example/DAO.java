package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DAO {

    String[] regions = {"xsmb", "xsmt", "xsmn"};

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

    public List<KQXS> resultMB() {
        List<KQXS> result = new ArrayList<>();
        String region = "Miền Bắc";
        try {
            Document document = Jsoup.connect("https://xskt.com.vn/xsmb").get();
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


    public List<KQXS> resultMT() {
        List<KQXS> test = new ArrayList<>();
        try {
            Document document = Jsoup.connect("https://xskt.com.vn/xsmt").get();
            Element el = document.getElementsByClass("box-ketqua").get(0);

            String province1 = el.select("tr").get(0).select("th").get(1).text();
            String province2 = el.select("tr").get(0).select("th").get(2).text();
            String[] provinces = {province1, province2};

            // date
            String dateHtml = el.select("h2").get(0).select("a").get(1).attr("href");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'/xsmt'/'ngay'-dd-MM-yyyy");
            LocalDate localDate = LocalDate.parse(dateHtml, formatter);
            String date = localDate.getDayOfMonth() + "-" + localDate.getMonthValue() + "-" + localDate.getYear();

            for (int i = 0; i < provinces.length; i++) {
                String DB_name = el.select("tr").get(9).select("td").get(0).attr("title");
                String G1_name = el.select("tr").get(8).select("td").get(0).attr("title");
                String G2_name = el.select("tr").get(7).select("td").get(0).attr("title");
                String G3_name = el.select("tr").get(6).select("td").get(0).attr("title");
                String G4_name = el.select("tr").get(5).select("td").get(0).attr("title");
                String G5_name = el.select("tr").get(4).select("td").get(0).attr("title");
                String G6_name = el.select("tr").get(3).select("td").get(0).attr("title");
                String G7_name = el.select("tr").get(2).select("td").get(0).attr("title");
                String G8_name = el.select("tr").get(1).select("td").get(0).attr("title");
                if (i == 0) {
                    String DB_number1 = el.select("tr").get(9).select("td").get(1).text();
                    test.add(new KQXS("Miền Trung", provinces[0], DB_name, DB_number1, date));

                    String G1_number = el.select("tr").get(8).select("td").get(1).text();
                    test.add(new KQXS("Miền Trung", provinces[0], G1_name, G1_number, date));

                    String G2_number = el.select("tr").get(7).select("td").get(1).text();
                    test.add(new KQXS("Miền Trung", provinces[0], G2_name, G2_number, date));

                    String G3_number = el.select("tr").get(6).select("td").get(1).text();
                    String[] G3_numbers = extractNumbers(G3_number);
                    for (String g : G3_numbers) {
                        test.add(new KQXS("Miền Trung", provinces[0], G3_name, g, date));
                    }

                    String G4_number = el.select("tr").get(5).select("td").get(1).text();
                    String[] G4_numbers = extractNumbers(G4_number);
                    for (String g : G4_numbers) {
                        test.add(new KQXS("Miền Trung", provinces[0], G4_name, g, date));
                    }

                    String G5_number = el.select("tr").get(4).select("td").get(1).text();
                    test.add(new KQXS("Miền Trung", provinces[0], G5_name, G5_number, date));

                    String G6_number = el.select("tr").get(3).select("td").get(1).text();
                    String[] G6_numbers = extractNumbers(G6_number);
                    for (String g : G6_numbers) {
                        test.add(new KQXS("Miền Trung", provinces[0], G6_name, g, date));
                    }

                    String G7_number = el.select("tr").get(2).select("td").get(1).text();
                    test.add(new KQXS("Miền Trung", provinces[0], G7_name, G7_number, date));

                    String G8_number = el.select("tr").get(1).select("td").get(1).text();
                    test.add(new KQXS("Miền Trung", provinces[0], G8_name, G8_number, date));

                } else if (i == 1) {
                    String DB_number2 = el.select("tr").get(9).select("td").get(2).text();
                    test.add(new KQXS("Miền Trung", provinces[1], DB_name, DB_number2, date));

                    String G1_number = el.select("tr").get(8).select("td").get(2).text();
                    test.add(new KQXS("Miền Trung", provinces[1], G1_name, G1_number, date));

                    String G2_number = el.select("tr").get(7).select("td").get(2).text();
                    test.add(new KQXS("Miền Trung", provinces[1], G2_name, G2_number, date));

                    String G3_number = el.select("tr").get(6).select("td").get(2).text();
                    String[] G3_numbers = extractNumbers(G3_number);
                    for (String g : G3_numbers) {
                        test.add(new KQXS("Miền Trung", provinces[1], G3_name, g, date));
                    }

                    String G4_number = el.select("tr").get(5).select("td").get(2).text();
                    String[] G4_numbers = extractNumbers(G4_number);
                    for (String g : G4_numbers) {
                        test.add(new KQXS("Miền Trung", provinces[1], G4_name, g, date));
                    }

                    String G5_number = el.select("tr").get(4).select("td").get(2).text();
                    test.add(new KQXS("Miền Trung", provinces[1], G5_name, G5_number, date));

                    String G6_number = el.select("tr").get(3).select("td").get(2).text();
                    String[] G6_numbers = extractNumbers(G6_number);
                    for (String g : G6_numbers) {
                        test.add(new KQXS("Miền Trung", provinces[1], G6_name, g, date));
                    }

                    String G7_number = el.select("tr").get(2).select("td").get(2).text();
                    test.add(new KQXS("Miền Trung", provinces[1], G7_name, G7_number, date));

                    String G8_number = el.select("tr").get(1).select("td").get(2).text();
                    test.add(new KQXS("Miền Trung", provinces[1], G8_name, G8_number, date));

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return test;
    }

    public List<KQXS> resultMN() {
        List<KQXS> test = new ArrayList<>();
        try {
            Document document = Jsoup.connect("https://xskt.com.vn/xsmn").get();
            Element el = document.getElementsByClass("box-ketqua").get(0);

            String province1 = el.select("tr").get(0).select("th").get(1).text();
            String province2 = el.select("tr").get(0).select("th").get(2).text();
            String province3 = el.select("tr").get(0).select("th").get(3).text();
            String[] provinces = {province1, province2, province3};

            // region
            String region = "Miền Nam";

            // date
            String dateHtml = el.select("h2").get(0).select("a").get(1).attr("href");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'/xsmn'/'ngay'-dd-MM-yyyy");
            LocalDate localDate = LocalDate.parse(dateHtml, formatter);
            String date = localDate.getDayOfMonth() + "-" + localDate.getMonthValue() + "-" + localDate.getYear();

            for (int i = 0; i < provinces.length; i++) {
                String DB_name = el.select("tr").get(9).select("td").get(0).attr("title");
                String G1_name = el.select("tr").get(8).select("td").get(0).attr("title");
                String G2_name = el.select("tr").get(7).select("td").get(0).attr("title");
                String G3_name = el.select("tr").get(6).select("td").get(0).attr("title");
                String G4_name = el.select("tr").get(5).select("td").get(0).attr("title");
                String G5_name = el.select("tr").get(4).select("td").get(0).attr("title");
                String G6_name = el.select("tr").get(3).select("td").get(0).attr("title");
                String G7_name = el.select("tr").get(2).select("td").get(0).attr("title");
                String G8_name = el.select("tr").get(1).select("td").get(0).attr("title");
                if (i == 0) {
                    String DB_number1 = el.select("tr").get(9).select("td").get(1).text();
                    test.add(new KQXS(region, provinces[0], DB_name, DB_number1, date));

                    String G1_number = el.select("tr").get(8).select("td").get(1).text();
                    test.add(new KQXS(region, provinces[0], G1_name, G1_number, date));

                    String G2_number = el.select("tr").get(7).select("td").get(1).text();
                    test.add(new KQXS(region, provinces[0], G2_name, G2_number, date));

                    String G3_number = el.select("tr").get(6).select("td").get(1).text();
                    String[] G3_numbers = extractNumbers(G3_number);
                    for (String g : G3_numbers) {
                        test.add(new KQXS(region, provinces[0], G3_name, g, date));
                    }

                    String G4_number = el.select("tr").get(5).select("td").get(1).text();
                    String[] G4_numbers = extractNumbers(G4_number);
                    for (String g : G4_numbers) {
                        test.add(new KQXS(region, provinces[0], G4_name, g, date));
                    }

                    String G5_number = el.select("tr").get(4).select("td").get(1).text();
                    test.add(new KQXS(region, provinces[0], G5_name, G5_number, date));

                    String G6_number = el.select("tr").get(3).select("td").get(1).text();
                    String[] G6_numbers = extractNumbers(G6_number);
                    for (String g : G6_numbers) {
                        test.add(new KQXS(region, provinces[0], G6_name, g, date));
                    }

                    String G7_number = el.select("tr").get(2).select("td").get(1).text();
                    test.add(new KQXS(region, provinces[0], G7_name, G7_number, date));

                    String G8_number = el.select("tr").get(1).select("td").get(1).text();
                    test.add(new KQXS(region, provinces[0], G8_name, G8_number, date));

                } else if (i == 1) {
                    String DB_number2 = el.select("tr").get(9).select("td").get(2).text();
                    test.add(new KQXS(region, provinces[1], DB_name, DB_number2, date));

                    String G1_number = el.select("tr").get(8).select("td").get(2).text();
                    test.add(new KQXS(region, provinces[1], G1_name, G1_number, date));

                    String G2_number = el.select("tr").get(7).select("td").get(2).text();
                    test.add(new KQXS(region, provinces[1], G2_name, G2_number, date));

                    String G3_number = el.select("tr").get(6).select("td").get(2).text();
                    String[] G3_numbers = extractNumbers(G3_number);
                    for (String g : G3_numbers) {
                        test.add(new KQXS(region, provinces[1], G3_name, g, date));
                    }

                    String G4_number = el.select("tr").get(5).select("td").get(2).text();
                    String[] G4_numbers = extractNumbers(G4_number);
                    for (String g : G4_numbers) {
                        test.add(new KQXS(region, provinces[1], G4_name, g, date));
                    }

                    String G5_number = el.select("tr").get(4).select("td").get(2).text();
                    test.add(new KQXS(region, provinces[1], G5_name, G5_number, date));

                    String G6_number = el.select("tr").get(3).select("td").get(2).text();
                    String[] G6_numbers = extractNumbers(G6_number);
                    for (String g : G6_numbers) {
                        test.add(new KQXS(region, provinces[1], G6_name, g, date));
                    }

                    String G7_number = el.select("tr").get(2).select("td").get(2).text();
                    test.add(new KQXS(region, provinces[1], G7_name, G7_number, date));

                    String G8_number = el.select("tr").get(1).select("td").get(2).text();
                    test.add(new KQXS(region, provinces[1], G8_name, G8_number, date));

                } else if (i == 2) {
                    String DB_number2 = el.select("tr").get(9).select("td").get(3).text();
                    test.add(new KQXS(region, provinces[2], DB_name, DB_number2, date));

                    String G1_number = el.select("tr").get(8).select("td").get(3).text();
                    test.add(new KQXS(region, provinces[2], G1_name, G1_number, date));

                    String G2_number = el.select("tr").get(7).select("td").get(3).text();
                    test.add(new KQXS(region, provinces[2], G2_name, G2_number, date));

                    String G3_number = el.select("tr").get(6).select("td").get(3).text();
                    String[] G3_numbers = extractNumbers(G3_number);
                    for (String g : G3_numbers) {
                        test.add(new KQXS(region, provinces[2], G3_name, g, date));
                    }

                    String G4_number = el.select("tr").get(5).select("td").get(3).text();
                    String[] G4_numbers = extractNumbers(G4_number);
                    for (String g : G4_numbers) {
                        test.add(new KQXS(region, provinces[2], G4_name, g, date));
                    }

                    String G5_number = el.select("tr").get(4).select("td").get(3).text();
                    test.add(new KQXS(region, provinces[2], G5_name, G5_number, date));

                    String G6_number = el.select("tr").get(3).select("td").get(3).text();
                    String[] G6_numbers = extractNumbers(G6_number);
                    for (String g : G6_numbers) {
                        test.add(new KQXS(region, provinces[2], G6_name, g, date));
                    }

                    String G7_number = el.select("tr").get(2).select("td").get(3).text();
                    test.add(new KQXS(region, provinces[2], G7_name, G7_number, date));

                    String G8_number = el.select("tr").get(1).select("td").get(3).text();
                    test.add(new KQXS(region, provinces[2], G8_name, G8_number, date));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return test;
    }

    public List<KQXS> getData() {
        List<KQXS> result = new ArrayList<>();

        try {
            for (String region : regions) {
                if (region.equals("xsmb")) {
                    List<KQXS> mb = resultMB();
                    for (KQXS k : mb) {
                        result.add(k);
                    }

                } else if (region.equals("xsmn")) {
                    List<KQXS> mn = resultMN();
                    for (KQXS k : mn) {
                        result.add(k);
                    }
                } else if (region.equals("xsmt")) {
                    List<KQXS> mt = resultMT();
                    for (KQXS k : mt) {
                        result.add(k);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void exportFileExcel() {
        List<KQXS> kqxs = getData();
        String[] columnsTitle = {"Region", "Province", "Award", "Number", "Date"};
        String excelPath = "KQXS.xlsx";

        try {
            File file = new File(excelPath);
            if (!file.exists()) {
                Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(excelPath);
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
            } else if(file.exists()) {
                System.out.println(file + " is exist!");
                file.delete();
                System.out.println("Delete file success");

                Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(excelPath);

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

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //
    public void insertDataToStaging(){
        exportFileExcel();
        String path = "KQXS.xlsx";
        try {
            Connection connection = new DBConnection().getConnection("staging");
            FileInputStream fis = new FileInputStream(path);

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

            PreparedStatement ps = connection.prepareStatement(query);
            for (int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if (row!=null){
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

            int[] rowQuantity = ps.executeBatch();
            System.out.println("Row quantity: " + rowQuantity.length);
            System.out.println("Insert success");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
