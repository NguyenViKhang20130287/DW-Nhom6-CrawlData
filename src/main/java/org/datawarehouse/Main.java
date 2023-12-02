package org.datawarehouse;

import org.datawarehouse.dao.ConfigurationDAO;
import org.datawarehouse.dao.InsertDataDAO;
import org.datawarehouse.dao.RunProcedureDAO;
import org.datawarehouse.entity.DataFileConfigs;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<DataFileConfigs> configs = new ConfigurationDAO().getDataConfigWithFlag();
        for (DataFileConfigs config : configs) {
            new InsertDataDAO().insertDataToStaging(config.getId(), config.getUsername(),
                    config.getPassword(), config.getLocation(), config.getSource_path());
            new RunProcedureDAO().transformData(config.getId(), config.getUsername(), config.getPassword());
            new RunProcedureDAO().insertDataToMart(config.getId(), config.getUsername(), config.getPassword());
        }

    }
}