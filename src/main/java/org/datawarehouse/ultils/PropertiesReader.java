package org.datawarehouse.ultils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {
    Properties properties;
    String path = "control.properties";

    public PropertiesReader() {
        properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            properties.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }
}
