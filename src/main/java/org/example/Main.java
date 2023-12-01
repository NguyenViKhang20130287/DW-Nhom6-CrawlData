package org.example;

import org.example.DAO.ConfigurationDAO;
import org.example.DAO.CrawlerDAO;
import org.example.DAO.ProcedureDAO;
import org.example.entity.DataFileConfigs;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<DataFileConfigs> config = new ConfigurationDAO().getDataConfigWithFlag();
        for(DataFileConfigs con : config){
            new CrawlerDAO().insertDataToStaging(con.getSource_path(), con.getLocation(), con.getId());
            new ProcedureDAO().transformData(con.getId());
            new ProcedureDAO().insertDataToMart(con.getId());
        }

//            System.out.println(new CrawlerDAO().resultMTMN("https://xskt.com.vn/"));


    }
}