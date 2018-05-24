package com.qf.rwxchina.xiaochefudriver.Bean;

/**
 * Created by hrr
 * on 2017/2/13.
 */
public class BaoDanAddress {
    private String city;
    private String address;
    private String detailedAddress;
    private Double lng;
    private Double lat;

    public BaoDanAddress() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetailedAddress() {
        return detailedAddress;
    }

    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress;
    }
}
