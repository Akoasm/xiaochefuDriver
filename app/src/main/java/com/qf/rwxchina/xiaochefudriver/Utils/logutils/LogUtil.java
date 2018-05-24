package com.qf.rwxchina.xiaochefudriver.Utils.logutils;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by Administrator on 2017/7/17.
 * Log打印工具类
 */

public class LogUtil {
    public static String TAG="hrr";
    public static void initNone(){
        Logger.init(TAG).logLevel(LogLevel.NONE);//不打印log，发布时使用
    }

    public static void initFull(){
        Logger.init(TAG).logLevel(LogLevel.FULL);//打印log，调试时使用
    }

    /**
     * 打印error级别
     * @param msg
     */
    public static void e(String msg){
        Logger.e(msg);
    }

    /**
     * 打印error级别，可自定义tag
     * @param tag
     * @param msg
     */
    public static void e(String tag,String msg){
        Logger.t(tag).e(msg);
    }

    /**
     * 打印json
     * @param json
     */
    public static void json(String json){
        Logger.json(json);
    }

    /**
     * 打印json，可自定义tag
     * @param tag
     * @param json
     */
    public static void json(String tag,String json){
        Logger.t(tag).json(json);
    }

    /**
     * 打印xml
     * @param xml
     */
    public static void xml(String xml){
        Logger.xml(xml);
    }

    /**
     * 打印xml，可自定义tag
     * @param tag
     * @param xml
     */
    public static void xml(String tag,String xml){
        Logger.t(tag).xml(xml);
    }


}
