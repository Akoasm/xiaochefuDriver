package com.qf.rwxchina.xiaochefudriver.Bean;

/**
 * 用户信息
 */
public class UserInfo {
    private int id;
    private int type_id;
    private String userurl;
    private String name;
    private String urgentphone;
    private String destination;
    private String balance;
    private int integral;
    private int isshake;
    private String phone;
    private String unicode;

    public UserInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIsshake() {
        return isshake;
    }

    public void setIsshake(int isshake) {
        this.isshake = isshake;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getUrgentphone() {
        return urgentphone;
    }

    public void setUrgentphone(String urgentphone) {
        this.urgentphone = urgentphone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserurl() {
        return userurl;
    }

    public void setUserurl(String userurl) {
        this.userurl = userurl;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }
}
