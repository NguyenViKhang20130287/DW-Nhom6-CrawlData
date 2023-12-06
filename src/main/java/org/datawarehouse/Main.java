package org.datawarehouse;

import org.datawarehouse.dao.ConfigurationDAO;
import org.datawarehouse.dao.CrawlerDAO;
import org.datawarehouse.dao.InsertDataDAO;
import org.datawarehouse.dao.RunProcedureDAO;
import org.datawarehouse.entity.DataFileConfigs;

import java.util.List;

public class Main {

    public void run(String date) {
        List<DataFileConfigs> configs = new ConfigurationDAO().getDataConfigWithFlag();
        RunProcedureDAO runProcedureDAO = new RunProcedureDAO();

        for (DataFileConfigs config : configs) {
            String status = new ConfigurationDAO().getDataFilesWithFileTimeStampNewestNoErr(config.getId()).getStatus();
            if (status == null) {
                new CrawlerDAO().exportFileExcel(config.getId(), config.getLocation(), config.getSource_path(), config.getFile_name(), date);
                new InsertDataDAO().insertDataToStaging(config.getId(), config.getUsername(), config.getPassword(), config.getLocation(), config.getFile_name());
                runProcedureDAO.transformData(config.getId(), config.getUsername(), config.getPassword());
                runProcedureDAO.insertDataToFact(config.getId(), config.getUsername(), config.getPassword());
                runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
            } else {
                try {
                    switch (status) {
                        case "FINISH":
                        case "CRAWLING":
                            new CrawlerDAO().exportFileExcel(config.getId(), config.getLocation(), config.getSource_path(), config.getFile_name(), date);
                            new InsertDataDAO().insertDataToStaging(config.getId(), config.getUsername(), config.getPassword(), config.getLocation(), config.getFile_name());
                            runProcedureDAO.transformData(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToFact(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                        case "CRAWLED":
                        case "EXTRACTING":
                            new InsertDataDAO().insertDataToStaging(config.getId(), config.getUsername(), config.getPassword(), config.getLocation(), config.getFile_name());
                            runProcedureDAO.transformData(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToFact(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                        case "EXTRACTED":
                        case "TRANSFORMING":
                            runProcedureDAO.transformData(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToFact(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                        case "TRANSFORMED":
                        case "FACT-LOADING":
                            runProcedureDAO.insertDataToFact(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                        case "FACT-LOADED":
                        case "AGGREGATE-LOADING":
                            runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                        case "AGGREGATE-LOADED":
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void main(String[] args) {

        // RUN 30 DAY
//        String[] days = {
//                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
//                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
//                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"
//        };
//        String[] days = {
//                "1", "2", "3", "4"
//        };
//
//        for (String day : days) {
//            new Main().run(day + "-12-2023");
//        }

        //
        new Main().run("");

    }

}