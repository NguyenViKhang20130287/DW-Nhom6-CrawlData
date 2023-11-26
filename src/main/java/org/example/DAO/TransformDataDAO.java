package org.example.DAO;

import org.example.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;

public class TransformDataDAO {
    public void runProcedure(int df_id) {
        new ConfigurationDAO().insertStatusConfig(df_id, "TRANSFORMING");
        String[] nameFuncs = {"ThemDuLieuMienDim", "ThemDuLieuTinhDim", "ThemDuLieuGiaiDim", "ThemDuLieuKetQuaXoSoFact"};
        try {
            Connection connection = new DBConnection().getConnection("staging");
            for (String nameFunc : nameFuncs){
                CallableStatement callableStatement = connection.prepareCall("{call " + nameFunc + "()}");

                callableStatement.execute();
                System.out.println("Run: " + nameFunc + " success");
            }
            connection.close();
            new ConfigurationDAO().insertStatusConfig(df_id, "TRANSFORMED");
        } catch (Exception e) {

        }
    }
}
