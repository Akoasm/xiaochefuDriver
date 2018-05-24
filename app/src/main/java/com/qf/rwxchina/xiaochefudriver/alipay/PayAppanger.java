package com.qf.rwxchina.xiaochefudriver.alipay;

/**
 * Created by Administrator on 2017/4/17 0017.
 */
public class PayAppanger {

    private String goods_name;
    private String date;
    private String money;

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public PayAppanger() {
    }

    private static PayAppanger instance = null;

    public static PayAppanger getInstance() {
        if (instance == null) {
            instance = new PayAppanger();
        }
        return instance;
    }
}