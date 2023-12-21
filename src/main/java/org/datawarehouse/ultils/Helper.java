package org.datawarehouse.ultils;

import org.datawarehouse.entity.KQXS;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
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
    public String extractDateInDataFiles(String input){
        // Define the regex pattern to match the date
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

        // Create a matcher with the input string
        Matcher matcher = pattern.matcher(input);

        // Check if the pattern is found
        if (matcher.find()) {
            // Extract the matched date
            String extractedDate = matcher.group(0);
           return extractedDate;
        }
        return null;
    }

    public String dateCrawl() throws Exception {
        String result = "";
        //
        String runDate = new PropertiesReader().getProperty("run.date");
        String dateInput = new PropertiesReader().getProperty("run.date.value");
        //
        Document document = Jsoup.connect("https://xskt.com.vn/").get();
        Element el = document.getElementsByClass("result").get(0);
        String dateHtml = el.select("tr").get(0).select("th").get(0).select("i").get(0).attr("title");
        if (runDate.equals("on")) {
            result = dateInput;
        } else if (runDate.equals("off")) {
            result = extractDate(dateHtml);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new Helper().dateCrawl());
    }
}
