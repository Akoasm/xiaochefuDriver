package com.qf.rwxchina.xiaochefudriver.Home;

/**
 * Created by longkeyou on 2017/4/24.
 * 单例例子
 */
public class Demo {
    private String str;
    private String str1;
    private String str2;
    private static Demo instance;

         public static Demo getInstance() {
             if (instance == null) {
                     instance = new Demo();
                }
             return instance;
             }


    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public String getStr2() {
        return str2;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }
}
