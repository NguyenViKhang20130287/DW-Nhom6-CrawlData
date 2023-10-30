package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    public static String[] extractNumbers(String input) {
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

    public static String getRegionName(String text){
        String result = "";
        if (text.equals("XSMN")){
            result = "Miền Nam";
        }
        if (text.equals("XSMB")){
            result = "Miền Bắc";
        }
        if (text.equals("XSMT")){
            result = "Miền Trung";
        }
        return result;
    }

    public static void main(String[] args) {

        System.out.println("Hello world!");
        String provinceUrl = "xscm";
        String url = "https://xskt.com.vn/" + provinceUrl;
        try {
            Document document = Jsoup.connect(url).get();
            Element el = document.getElementsByClass("result").get(0);
            String region = document.getElementsByClass("has-sub").get(1).select("a").get(0).text();

            String giai_DB = el.select("tr").get(10).select("td").get(1).text();

            String giai_1 = el.select("tr").get(9).select("td").get(1).text();

            String giai_2 = el.select("tr").get(8).select("td").get(1).text();

            String giai_3 = el.select("tr").get(7).select("td").get(1).text();
            String[] ds_giai_3 = extractNumbers(giai_3);

            String giai_4 = el.select("tr").get(5).select("td").get(1).text();
            String[] ds_giai_4 = extractNumbers(giai_4);

            String giai_5 = el.select("tr").get(4).select("td").get(1).text();

            String giai_6 = el.select("tr").get(3).select("td").get(1).text();
            String[] ds_giai_6 = extractNumbers(giai_6);

            String giai_7 = el.select("tr").get(2).select("td").get(1).text();

            String giai_8 = el.select("tr").get(1).select("td").get(1).text();

            System.out.println(getRegionName(region));
            System.out.println("---------------------");
            System.out.println("Dac Biet: " + giai_DB);
            System.out.println("---------------------");
            System.out.println("Giai nhat: " + giai_1);
            System.out.println("---------------------");
            System.out.println("Giai nhi: " + giai_2);
            System.out.println("---------------------");
            for (String s_giai_3 : ds_giai_3) {
                System.out.println("Giai ba: " + s_giai_3);
            }
            System.out.println("---------------------");
            for (String s_giai_4 : ds_giai_4) {
                System.out.println("Giai tu: " + s_giai_4);
            }
            System.out.println("---------------------");
            System.out.println("Giai nam: " + giai_5);
            System.out.println("---------------------");
            for (String s_giai_6 : ds_giai_6) {
                System.out.println("Giai sau: " + s_giai_6);
            }
            System.out.println("---------------------");
            System.out.println("Giai bay: " + giai_7);
            System.out.println("---------------------");
            System.out.println("Giai tam: " + giai_8);
            System.out.println("---------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}