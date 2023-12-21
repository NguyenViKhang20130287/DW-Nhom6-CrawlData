package org.datawarehouse;

import org.datawarehouse.entity.DataFileConfigs;
import org.datawarehouse.module.*;

import java.util.List;

public class Main {

//    public void run() {
//        // 3. Lấy ra tất cả các dòng có flag = 1 trong control.data_file_configs
//        List<DataFileConfigs> configs = new Configuration().getDataConfigWithFlag();
//
//        // 4. Lấy 1 dòng join với control.data_files
//        for (DataFileConfigs config : configs) {
//            // 5. Kiểm tra status dòng có file_timestamp mới nhất
//            String status = new Configuration().getDataFilesWithFileTimeStampNewestNoErr(config.getId()).getStatus();
//            // 6. status = NULL?
//            if (status == null) {
//                // 9. Crawler data
//                new CrawlData().exportFileExcel(config.getId(), config.getLocation(), config.getSource_path(),
//                        config.getFile_name(), config.getFormat());
//                // 10. EXTRACT file excel vào staging
//                new ExtractData().start(config.getId(), config.getUsername(), config.getPassword(),
//                         config.getLocation(), config.getFile_name(), config.getFormat());
//                // 11. Transform data
//                new TransformData().start(config.getId(), config.getUsername(), config.getPassword());
//                // 12. Thêm dữ liệu vào warehouse
//                new InsertDataToFact().start(config.getId(), config.getUsername(), config.getPassword());
//                // 13. Aggregate data
//                new AggregateData().start(config.getId(), config.getUsername(), config.getPassword());
//                // 14. Thêm dữ liệu vào data_mart
//                new InsertDataToMart().start(config.getId(), config.getUsername(), config.getPassword());
//            } else {
//                try {
//                    switch (status) {
//                        // 7. status = FINISH ?
//                        case "FINISH":
//                            // 8. status = CRAWLING ?
//                        case "CRAWLING":
//                            // 9. Crawler data
//                            new CrawlData().exportFileExcel(config.getId(), config.getLocation(), config.getSource_path(),
//                                    config.getFile_name(), config.getFormat());
//                            // 10. EXTRACT file excel vào staging
//                            new ExtractData().start(config.getId(), config.getUsername(), config.getPassword(),
//                                     config.getLocation(), config.getFile_name(), config.getFormat());
//                            // 11. Transform data
//                            new TransformData().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 12. Thêm dữ liệu vào warehouse
//                            new InsertDataToFact().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 13. Aggregate data
//                            new AggregateData().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 14. Thêm dữ liệu vào data_mart
//                            new InsertDataToMart().start(config.getId(), config.getUsername(), config.getPassword());
//                            break;
//                            // 15. status = CRAWLED ?
//                        case "CRAWLED":
//                            // 16. status = EXTRACTING ?
//                        case "EXTRACTING":
//                            // 10. EXTRACT file excel vào staging
//                            new ExtractData().start(config.getId(), config.getUsername(), config.getPassword(),
//                                     config.getLocation(), config.getFile_name(), config.getFormat());
//                            // 11. Transform data
//                            new TransformData().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 12. Thêm dữ liệu vào warehouse
//                            new InsertDataToFact().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 13. Aggregate data
//                            new AggregateData().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 14. Thêm dữ liệu vào data_mart
//                            new InsertDataToMart().start(config.getId(), config.getUsername(), config.getPassword());
//                            break;
//                            // 17. status = EXTRACTED ?
//                        case "EXTRACTED":
//                            // 18. status = TRANSFORMING ?
//                        case "TRANSFORMING":
//                            // 11. Transform data
//                            new TransformData().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 12. Thêm dữ liệu vào warehouse
//                            new InsertDataToFact().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 13. Aggregate data
//                            new AggregateData().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 14. Thêm dữ liệu vào data_mart
//                            new InsertDataToMart().start(config.getId(), config.getUsername(), config.getPassword());
//                            break;
//                            // 19. status = TRANSFORMED ?
//                        case "TRANSFORMED":
//                            // 20. status = FACT-LOADING ?
//                        case "FACT-LOADING":
//                            // 12. Thêm dữ liệu vào warehouse
//                            new InsertDataToFact().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 13. Aggregate data
//                            new AggregateData().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 14. Thêm dữ liệu vào data_mart
//                            new InsertDataToMart().start(config.getId(), config.getUsername(), config.getPassword());
//                            break;
//                            // 21. status = FACT-LOADED ?
//                        case "FACT-LOADED":
//                            // 22. status = AGGREGATE-LOADING ?
//                        case "AGGREGATE-LOADING":
//                            // 13. Aggregate data
//                            new AggregateData().start(config.getId(), config.getUsername(), config.getPassword());
//                            // 14. Thêm dữ liệu vào data_mart
//                            new InsertDataToMart().start(config.getId(), config.getUsername(), config.getPassword());
//                            break;
//                            // 23. status = AGGREGATE-LOADED ?
//                        case "AGGREGATE-LOADED":
//                            // 24. status = MART-LOADING ?
//                        case "MART-LOADING":
//                            // 14. Thêm dữ liệu vào data_mart
//                            new InsertDataToMart().start(config.getId(), config.getUsername(), config.getPassword());
//                            break;
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }

    public static void main(String[] args) throws Exception {
//        new Main().run();


    }

}