package com.qf.rwxchina.xiaochefudriver.Bean;

/**
 * Created by zkl on 2016/9/22.
 */
public class DriverInfo {
    private int driverid;
    private String head_image;
    private String name;
    private int avglevel;
    private int driving_years;
    private int work_status;
    private int agentsum;
    private double lng;
    private double lat;
    private String distance;
    private String work_number;

    public DriverInfo() {
    }

    public String getWork_number() {
        return work_number;
    }

    public void setWork_number(String work_number) {
        this.work_number = work_number;
    }

    public int getDriverid() {
        return driverid;
    }

    public void setDriverid(int driverid) {
        this.driverid = driverid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getAgentsum() {
        return agentsum;
    }

    public void setAgentsum(int agentsum) {
        this.agentsum = agentsum;
    }

    public int getWork_status() {
        return work_status;
    }

    public void setWork_status(int work_status) {
        this.work_status = work_status;
    }

    public int getDriving_years() {
        return driving_years;
    }

    public void setDriving_years(int driving_years) {
        this.driving_years = driving_years;
    }

    public int getAvglevel() {
        return avglevel;
    }

    public void setAvglevel(int avglevel) {
        this.avglevel = avglevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead_image() {
        return head_image;
    }

    public void setHead_image(String head_image) {
        this.head_image = head_image;
    }
}
