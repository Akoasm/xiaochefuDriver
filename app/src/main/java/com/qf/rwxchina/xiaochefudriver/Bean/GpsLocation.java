package com.qf.rwxchina.xiaochefudriver.Bean;

public class GpsLocation {
    public double lat;//纬度
    public double lng;//经度

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

    @Override
    public String toString() {
        return "GpsLocation{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
