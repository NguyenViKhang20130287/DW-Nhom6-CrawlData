package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class GetResultWithDay {

    public List<KQXS> mienBac() {
        List<KQXS> result = new ArrayList<>();
        String region = "Miền Bắc";
        try {
            Document document = Jsoup.connect("https://xskt.com.vn/xsmb").get();
            Element el = document.getElementsByClass("result").get(0);
            //province
            String provinceHtml = el.select("tr").get(0).select("th").get(0).select("b").get(0).text();
            String province = new DAO().extractStringInParentheses(provinceHtml);

            // date
            String dateHtml = el.select("tr").get(0).select("th").get(0).select("i").get(0).attr("title");
            String date = new DAO().extractDate(dateHtml);

            String DB_name = el.select("tr").get(1).select("td").get(0).attr("title");
            String DB_number = el.select("tr").get(1).select("td").get(1).text();
            result.add(new KQXS(region, province, DB_name, DB_number, date));

            String G1_name = el.select("tr").get(2).select("td").get(0).attr("title");
            String G1_number = el.select("tr").get(2).select("td").get(1).text();
            result.add(new KQXS(region, province, G1_name, G1_number, date));

            String G2_name = el.select("tr").get(3).select("td").get(0).attr("title");
            String G2_number = el.select("tr").get(3).select("td").get(1).text();
            String[] G2_numbers = new DAO().extractNumbers(G2_number);
            for (String g : G2_numbers) {
                result.add(new KQXS(region, province, G2_name, g, date));
            }

            String G3_name = el.select("tr").get(4).select("td").get(0).attr("title");
            String G3_number = el.select("tr").get(4).select("td").get(1).text();
            String[] G3_numbers = new DAO().extractNumbers(G3_number);
            for (String g : G3_numbers) {
                result.add(new KQXS(region, province, G3_name, g, date));
            }

            String G4_name = el.select("tr").get(6).select("td").get(0).attr("title");
            String G4_number = el.select("tr").get(6).select("td").get(1).text();
            String[] G4_numbers = new DAO().extractNumbers(G4_number);
            for (String g : G4_numbers) {
                result.add(new KQXS(region, province, G4_name, g, date));
            }

            String G5_name = el.select("tr").get(7).select("td").get(0).attr("title");
            String G5_number = el.select("tr").get(7).select("td").get(1).text();
            String[] G5_numbers = new DAO().extractNumbers(G5_number);
            for (String g : G5_numbers) {
                result.add(new KQXS(region, province, G5_name, g, date));
            }

            String G6_name = el.select("tr").get(9).select("td").get(0).attr("title");
            String G6_number = el.select("tr").get(9).select("td").get(1).text();
            String[] G6_numbers = new DAO().extractNumbers(G6_number);
            for (String g : G6_numbers) {
                result.add(new KQXS(region, province, G6_name, g, date));
            }

            String G7_name = el.select("tr").get(10).select("td").get(0).attr("title");
            String G7_number = el.select("tr").get(10).select("td").get(1).text();
            String[] G7_numbers = new DAO().extractNumbers(G7_number);
            for (String g : G7_numbers) {
                result.add(new KQXS(region, province, G7_name, g, date));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
