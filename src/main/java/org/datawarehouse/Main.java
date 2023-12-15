package org.datawarehouse;

import org.datawarehouse.dao.ConfigurationDAO;
import org.datawarehouse.dao.CrawlerDAO;
import org.datawarehouse.dao.InsertDataDAO;
import org.datawarehouse.dao.RunProcedureDAO;
import org.datawarehouse.entity.DataFileConfigs;

import java.util.List;

public class Main {

    public void run(String date) {
        // 3. Lấy ra tất cả các dòng có flag = 1 trong control.data_file_configs
        List<DataFileConfigs> configs = new ConfigurationDAO().getDataConfigWithFlag();
        RunProcedureDAO runProcedureDAO = new RunProcedureDAO();

        // 4. Lấy 1 dòng join với control.data_files
        for (DataFileConfigs config : configs) {
            // 5. Kiểm tra status dòng có file_timestamp mới nhất
            String status = new ConfigurationDAO().getDataFilesWithFileTimeStampNewestNoErr(config.getId()).getStatus();
            // 6. status = NULL?
            if (status == null) {
                // 9. Crawler data
                new CrawlerDAO().exportFileExcel(config.getId(), config.getLocation(), config.getSource_path(), config.getFile_name(), date);
                // 10. EXTRACT file excel vào staging
                new InsertDataDAO().insertDataToStaging(config.getId(), config.getUsername(), config.getPassword(), config.getLocation(), config.getFile_name());
                // 11. Transform data
                runProcedureDAO.transformData(config.getId(), config.getUsername(), config.getPassword());
                // 12. Thêm dữ liệu vào warehouse
                runProcedureDAO.insertDataToFact(config.getId(), config.getUsername(), config.getPassword());
                // 13. Aggregate data
                runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                // 14. Thêm dữ liệu vào data_mart
                runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
            } else {
                try {
                    switch (status) {
                        // 7. status = FINISH ?
                        case "FINISH":
                            // 8. status = CRAWLING ?
                        case "CRAWLING":
                            new CrawlerDAO().exportFileExcel(config.getId(), config.getLocation(), config.getSource_path(), config.getFile_name(), date);
                            new InsertDataDAO().insertDataToStaging(config.getId(), config.getUsername(), config.getPassword(), config.getLocation(), config.getFile_name());
                            runProcedureDAO.transformData(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToFact(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                            // 15. status = CRAWLED ?
                        case "CRAWLED":
                            // 16. status = EXTRACTING ?
                        case "EXTRACTING":
                            new InsertDataDAO().insertDataToStaging(config.getId(), config.getUsername(), config.getPassword(), config.getLocation(), config.getFile_name());
                            runProcedureDAO.transformData(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToFact(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                            // 17. status = EXTRACTED ?
                        case "EXTRACTED":
                            // 18. status = TRANSFORMING ?
                        case "TRANSFORMING":
                            runProcedureDAO.transformData(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToFact(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                            // 19. status = TRANSFORMED ?
                        case "TRANSFORMED":
                            // 20. status = FACT-LOADING ?
                        case "FACT-LOADING":
                            runProcedureDAO.insertDataToFact(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                            // 21. status = FACT-LOADED ?
                        case "FACT-LOADED":
                            // 22. status = AGGREGATE-LOADING ?
                        case "AGGREGATE-LOADING":
                            runProcedureDAO.insertDataToAggregate(config.getId(), config.getUsername(), config.getPassword());
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                            // 23. status = AGGREGATE-LOADED ?
                        case "AGGREGATE-LOADED":
                            // 24. status = MART-LOADING ?
                        case "MART-LOADING":
                            runProcedureDAO.insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void main(String[] args) throws Exception {

        // RUN 30 DAY
//        String[] days = {
//                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
//                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
//                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"
//        };

//        String[] days = {
//                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
//                "11", "12", "13", "14"
//        };
//
//        for (String day : days) {
//            new Main().run(day + "-12-2023");
//        }

        //
        new Main().run("10-12-2023");

//        System.out.println(new CrawlerDAO().resultMTMN("https://xskt.com.vn/", ""));

    }

}