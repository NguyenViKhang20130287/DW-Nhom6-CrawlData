package org.example;

public class KQXS {

    private String region;
    private String province;
    private String award;
    private String number;
    private String date;

    public KQXS(String region, String province, String award, String number, String date) {
        this.region = region;
        this.province = province;
        this.award = award;
        this.number = number;
        this.date = date;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "KQXS{" +
                "region='" + region + '\'' +
                ", province='" + province + '\'' +
                ", award='" + award + '\'' +
                ", number='" + number + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
