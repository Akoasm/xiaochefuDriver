package com.qf.rwxchina.xiaochefudriver.Bean;

/**
 * 起步价
 */
public class TimeDuan {
    private String maxnumber;
    String money;
    String addmoney;
    String waittimeduan;
    String waitmoney;
    String begintime;
    String endtime;
    private String maxnumber_exc;
    private String waittimeduan_exc;
    public TimeDuan() {
    }

    public TimeDuan(String maxnumber, String money, String addmoney, String waittimeduan, String waitmoney, String begintime, String endtime, String maxnumber_exc, String waittimeduan_exc) {
        this.maxnumber = maxnumber;
        this.money = money;
        this.addmoney = addmoney;
        this.waittimeduan = waittimeduan;
        this.waitmoney = waitmoney;
        this.begintime = begintime;
        this.endtime = endtime;
        this.maxnumber_exc = maxnumber_exc;
        this.waittimeduan_exc = waittimeduan_exc;
    }

    public String getMaxnumber() {
        return maxnumber;
    }

    public void setMaxnumber(String maxnumber) {
        this.maxnumber = maxnumber;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getAddmoney() {
        return addmoney;
    }

    public void setAddmoney(String addmoney) {
        this.addmoney = addmoney;
    }

    public String getWaittimeduan() {
        return waittimeduan;
    }

    public void setWaittimeduan(String waittimeduan) {
        this.waittimeduan = waittimeduan;
    }

    public String getWaitmoney() {
        return waitmoney;
    }

    public void setWaitmoney(String waitmoney) {
        this.waitmoney = waitmoney;
    }

    public String getBegintime() {
        return begintime;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getMaxnumber_exc() {
        return maxnumber_exc;
    }

    public void setMaxnumber_exc(String maxnumber_exc) {
        this.maxnumber_exc = maxnumber_exc;
    }

    public String getWaittimeduan_exc() {
        return waittimeduan_exc;
    }

    public void setWaittimeduan_exc(String waittimeduan_exc) {
        this.waittimeduan_exc = waittimeduan_exc;
    }

    @Override
    public String toString() {
        return "TimeDuan{" +
                "maxnumber='" + maxnumber + '\'' +
                ", money='" + money + '\'' +
                ", addmoney='" + addmoney + '\'' +
                ", waittimeduan='" + waittimeduan + '\'' +
                ", waitmoney='" + waitmoney + '\'' +
                ", begintime='" + begintime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", maxnumber_exc='" + maxnumber_exc + '\'' +
                ", waittimeduan_exc='" + waittimeduan_exc + '\'' +
                '}';
    }
}
