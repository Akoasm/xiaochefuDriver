package com.qf.rwxchina.xiaochefudriver.Personal.entity;

/**
 * Created by Administrator on 2017/7/12.
 * 公告实体类
 */

public class Notice {
    private String msgid;//公告id
    private String title;//公告标题
    private String isread;//消息是否已读 0-没有 1-已读
    private String createtime;//时间
    public Notice(){}

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsread() {
        return isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "msgid='" + msgid + '\'' +
                ", title='" + title + '\'' +
                ", isread='" + isread + '\'' +
                ", createtime='" + createtime + '\'' +
                '}';
    }
}
