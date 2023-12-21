package org.datawarehouse.entity;

public class DataFiles {
    private int configId;
    private String file_timeStamp;
    private String status;
    private String create_at;
    private String name;

    public DataFiles(int configId, String file_timeStamp, String status, String create_at) {
        this.configId = configId;
        this.file_timeStamp = file_timeStamp;
        this.status = status;
        this.create_at = create_at;
    }

    public DataFiles() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public String getFile_timeStamp() {
        return file_timeStamp;
    }

    public void setFile_timeStamp(String file_timeStamp) {
        this.file_timeStamp = file_timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    @Override
    public String toString() {
        return "DataFiles{" +
                "configId=" + configId +
                ", file_timeStamp='" + file_timeStamp + '\'' +
                ", status='" + status + '\'' +
                ", create_at='" + create_at + '\'' + "\n" +
                '}';
    }
}
