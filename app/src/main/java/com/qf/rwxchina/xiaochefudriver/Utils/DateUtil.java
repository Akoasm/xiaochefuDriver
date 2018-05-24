package com.qf.rwxchina.xiaochefudriver.Utils;

import android.content.Context;
import android.telecom.TelecomManager;
import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/21.
 */
public class DateUtil {
    //判断是否通话中
    public static boolean phoneIsInUse(Context context){
        TelecomManager tm = (TelecomManager)context.getSystemService(Context.TELECOM_SERVICE);
        return  tm.isInCall();
    }
    /**
     * 获取当前时间
     * @return
     */
    public static String GetDate(){
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String newDate=sdf.format(date);
        return newDate;
    }
    /**
     * 当前日期增加一天后的日期
     * 格式 如：yyyy年MM月dd日 ，yyyy-MM-dd
     * @return
     */
    public static String GetDateAdd(Date date) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s=sdf.format(date);
        Timestamp time=Timestamp.valueOf(s);
        long t=time.getTime()+24*60*60*1000;
        time.setTime(t);
        SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
        String newDate = sdf2.format(time);
        return newDate;
    }
    /**
     * 当前日期减少一天后的日期
     * 格式 如：yyyy年MM月dd日 ，yyyy-MM-dd
     * @return
     */
    public static String GetDateMini(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s=sdf.format(date);
        Timestamp time=Timestamp.valueOf(s);
        long l=time.getTime()-24*60*60*1000;
        time.setTime(l);
        SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
        String newDate=sdf2.format(time);

        return newDate;
    }

    public static Date StringForDate(String date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date1=null;
        try {
            date1= sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }finally {
            return date1;
        }
    }

    /**
     * 时间戳转为时间(年月日，时分秒)
     *
     * @param cc_time 时间戳
     * @return
     */
    public static String getStrTime(String cc_time) {
        if (cc_time==null||"".equals(cc_time)){
            cc_time="0";
        }
        String re_StrTime = null;
        //同理也可以转为其它样式的时间格式.例如："yyyy/MM/dd HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /**
     * 时间转换为时间戳
     *
     * @param timeStr 时间 例如: 2016-03-09
     * @param format  时间对应格式  例如: yyyy-MM-dd
     * @return
     */
    public static long getTimeStamp(String timeStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(timeStr);
            long timeStamp = date.getTime();
            return timeStamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
