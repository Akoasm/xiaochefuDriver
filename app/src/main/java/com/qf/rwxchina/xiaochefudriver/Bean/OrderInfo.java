package com.qf.rwxchina.xiaochefudriver.Bean;

/**
 * 订单信息列表
 */
public class OrderInfo {
    private int id; //订单id
    private String out_trade_no;    //啥
    private String orderson;    //这又是啥
    private int ordertype;      //订单类型
    private int uid;            //用户id
    private int drivercount;    //需要司机数量
    private String saddress;    //开始地址
    private Double slng;        //开始地址的经纬度
    private Double slat;
    private String bespeaktime; //预约时间
    private String createtime;  //创建订单的时间
    private String phones;      //电话
    private String oaddress;    //终点地址
    private Double olng;
    private Double olat;
    private String indentdistance;  //距离
    private String uname;

    public OrderInfo() {
    }

    public OrderInfo(int id, String indentdistance, Double olat, Double olng, String oaddress, String phones, String createtime, String bespeaktime, Double slng, Double slat, String saddress, int drivercount, int uid, int ordertype, String orderson, String out_trade_no,String uname) {
        this.id = id;
        this.indentdistance = indentdistance;
        this.olat = olat;
        this.olng = olng;
        this.oaddress = oaddress;
        this.phones = phones;
        this.createtime = createtime;
        this.bespeaktime = bespeaktime;
        this.slng = slng;
        this.slat = slat;
        this.saddress = saddress;
        this.drivercount = drivercount;
        this.uid = uid;
        this.ordertype = ordertype;
        this.orderson = orderson;
        this.out_trade_no = out_trade_no;
        this.uname = uname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIndentdistance() {
        return indentdistance;
    }

    public void setIndentdistance(String indentdistance) {
        this.indentdistance = indentdistance;
    }

    public Double getOlat() {
        return olat;
    }

    public void setOlat(Double olat) {
        this.olat = olat;
    }

    public Double getOlng() {
        return olng;
    }

    public void setOlng(Double olng) {
        this.olng = olng;
    }

    public String getOaddress() {
        return oaddress;
    }

    public void setOaddress(String oaddress) {
        this.oaddress = oaddress;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    public String getBespeaktime() {
        return bespeaktime;
    }

    public void setBespeaktime(String bespeaktime) {
        this.bespeaktime = bespeaktime;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public Double getSlat() {
        return slat;
    }

    public void setSlat(Double slat) {
        this.slat = slat;
    }

    public Double getSlng() {
        return slng;
    }

    public void setSlng(Double slng) {
        this.slng = slng;
    }

    public String getSaddress() {
        return saddress;
    }

    public void setSaddress(String saddress) {
        this.saddress = saddress;
    }

    public int getDrivercount() {
        return drivercount;
    }

    public void setDrivercount(int drivercount) {
        this.drivercount = drivercount;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(int ordertype) {
        this.ordertype = ordertype;
    }

    public String getOrderson() {
        return orderson;
    }

    public void setOrderson(String orderson) {
        this.orderson = orderson;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}
