package org.datawarehouse.dao;

import org.datawarehouse.connection.DBConnection;
import org.datawarehouse.entity.DataFileConfigs;
import org.datawarehouse.entity.DataFiles;
import org.datawarehouse.ultils.PropertiesReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationDAO {

    Connection connection;
    DBConnection db = new DBConnection();
    PreparedStatement ps;
    ResultSet rs;
    EmailSenderDAO emailSenderDAO = new EmailSenderDAO();

    // PROPERTIES

    // 1. Lấy ra dữ liệu trong file propertires
    PropertiesReader pr = new PropertiesReader();
    String USERNAME = pr.getProperty("database.username");
    String PASSWORD = pr.getProperty("database.password");
    String DATABASE_NAME = pr.getProperty("database.name.control");

    // 3. Lấy ra tất cả các dòng có flag = 1 trong control.data_file_configs
    public List<DataFileConfigs> getDataConfigWithFlag() {
        List<DataFileConfigs> list = new ArrayList<>();
        try {
            // 2. Kết nối database control
            connection = db.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            String query = "SELECT * FROM `data_file_configs` where flag = 1 ";
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                DataFileConfigs data = new DataFileConfigs(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getString(8),
                        rs.getString(9), rs.getString(10), rs.getString(11));
                list.add(data);
            }

            // close
            rs.close();
            ps.close();
            db.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
            emailSenderDAO.sendEmail(e.getMessage());
        }
        return list;
    }

    public List<DataFiles> getDatafiles(int configId) {
        List<DataFiles> result = new ArrayList<>();
        String query = "SELECT df.df_config_id, df.file_timestamp, df.`status`, df.created_at " +
                "FROM data_files df WHERE df.df_config_id = ?";
        try {
            connection = db.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            ps = connection.prepareStatement(query);
            ps.setInt(1, configId);
            rs = ps.executeQuery();
            while (rs.next()) {
                DataFiles df = new DataFiles(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4));
                result.add(df);
            }
            rs.close();
            ps.close();
            db.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
            emailSenderDAO.sendEmail(e.getMessage());
        }
        return result;
    }

    public DataFiles getDataFilesWithFileTimeStampNewest(int configId) {
        DataFiles df = new DataFiles();
        String query = "SELECT df.df_config_id, df.file_timestamp, df.`status`, df.created_at FROM data_files df \n" +
                "WHERE df.id = (\n" +
                "    SELECT MAX(df1.id)\n" +
                "    FROM data_files df1\n" +
                ") AND df.df_config_id = ?\n" +
                "ORDER BY df.file_timestamp DESC\n" +
                "LIMIT 1;";
        try {
            connection = db.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            ps = connection.prepareStatement(query);
            ps.setInt(1, configId);
            rs = ps.executeQuery();
            while (rs.next()){
                df.setConfigId(rs.getInt(1));
                df.setFile_timeStamp(rs.getString(2));
                df.setStatus(rs.getString(3));
                df.setCreate_at(rs.getString(4));
            }
            rs.close();
            ps.close();
            db.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
            emailSenderDAO.sendEmail(e.getMessage());
        }
        return df;
    }

    // 5. Kiểm tra status dòng có file_timestamp mới nhất
    public DataFiles getDataFilesWithFileTimeStampNewestNoErr(int configId) {
        DataFiles df = new DataFiles();
        String query = "SELECT df.df_config_id, df.file_timestamp, df.`status`, df.created_at, df.id\n" +
                "FROM data_files df\n" +
                "WHERE df.`status` <> 'ERROR' AND df.df_config_id = ?\n" +
                "ORDER BY df.file_timestamp DESC, df.id DESC\n" +
                "LIMIT 1;\n";
        try {
            connection = db.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            ps = connection.prepareStatement(query);
            ps.setInt(1, configId);
            rs = ps.executeQuery();
            while (rs.next()){
                df.setConfigId(rs.getInt(1));
                df.setFile_timeStamp(rs.getString(2));
                df.setStatus(rs.getString(3));
                df.setCreate_at(rs.getString(4));
            }
            rs.close();
            ps.close();
            db.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
            emailSenderDAO.sendEmail(e.getMessage());
        }
        return df;
    }

    public void insertStatusConfig(int df_id, String status) {
        try {
            Connection connection = db.getConnection(DATABASE_NAME, USERNAME, PASSWORD);
            String query = "INSERT INTO data_files(data_files.df_config_id, " +
                    "data_files.`name`, data_files.`status`, " +
                    "data_files.file_timestamp, " +
                    "data_files.note, " +
                    "data_files.created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(query);
            ps.setInt(1, df_id);
            ps.setString(2, "KET QUA XO SO");
            ps.setString(3, status);
            ps.setString(4, String.valueOf(LocalDateTime.now()));
            ps.setString(5, "NOTE");
            ps.setString(6, String.valueOf(LocalDate.now()));
            ps.executeUpdate();
            System.out.println("Insert status: " + status + " success");

            // close
            ps.close();
            db.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
            emailSenderDAO.sendEmail(e.getMessage());
        }
    }
}
