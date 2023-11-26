package org.example.entity;

public class DataFileConfigs {
    private int id;
    private String name;
    private String description;
    private String source_path;
    private String location;
    private String flag;
    private String created_at;
    private String updated_at;

    public DataFileConfigs(int id, String name, String description, String source_path,
                           String location, String flag, String created_at, String updated_at) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.source_path = source_path;
        this.location = location;
        this.flag = flag;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource_path() {
        return source_path;
    }

    public void setSource_path(String source_path) {
        this.source_path = source_path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "DataFileConfigs{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", source_path='" + source_path + '\'' +
                ", location='" + location + '\'' +
                ", flag='" + flag + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
