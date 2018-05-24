package com.qf.rwxchina.xiaochefudriver.Personal;

/**
 * Created by Administrator on 2016/12/20.
 * 详细界面实体类
 */
public class MinuteInfo {
    String title;
    String content;

    public MinuteInfo(String title, String content) {
        this.title = title;
        this.content = content;
    }
     public MinuteInfo(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MinuteInfo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
