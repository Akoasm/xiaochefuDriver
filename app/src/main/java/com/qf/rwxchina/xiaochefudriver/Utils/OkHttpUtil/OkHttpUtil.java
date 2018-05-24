package com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil;

import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;
import com.qf.rwxchina.xiaochefudriver.Utils.toastutils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/7/17.
 * 网络请求工具类
 * 1.get、post请求（不自动解析bean对象）
 * 2.get、post请求（自动解析bean对象）
 * 3.上传文件
 * 4.下载文件
 */

public class OkHttpUtil {
    /**
     * 初始化
     */
    public static void init(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new RetryAndChangeIpInterceptor(3))
                .retryOnConnectionFailure(true)//允许请求失败重新请求
                .connectTimeout(50000L, TimeUnit.MILLISECONDS)
                .readTimeout(50000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * get请求，自动解析成实体类
     * @param url
     * @param map
     * @param tag
     * @param callBack
     */
    public static void get(String url, Map<String,String> map, Object tag, OkCallBack<?> callBack){
        OkHttpUtils.get()
                .url(url)
                .params(map)
                .tag(tag)
                .build()
                .execute(callBack);
    }

    /**
     * get请求，不解析成实体类
     * @param url
     * @param map
     * @param tag
     * @param callBack
     */
    public static void get(String url, Map<String,String> map, Object tag, OkStringCallBack callBack){
        OkHttpUtils.get()
                .url(url)
                .params(map)
                .tag(tag)
                .build()
                .execute(callBack);
    }

    /**
     * post请求，自动解析成实体类
     * @param url
     * @param map
     * @param tag
     * @param callBack
     */
    public static void post(String url, Map<String,String> map, Object tag, OkCallBack<?> callBack){
        OkHttpUtils.post()
                .url(url)
                .params(map)
                .tag(tag)
                .build()
                .execute(callBack);
    }

    /**
     * post请求，不自动解析实体类
     * @param url
     * @param map
     * @param tag
     * @param callBack
     */
    public static void post(String url, Map<String,String> map, Object tag, OkStringCallBack callBack){
        if (!Network.HttpTest(MyApplication.getContext())){
            ToastUtil.showShort("没网络了哦，请检查网络");
        }else {
            OkHttpUtils.post()
                    .url(url)
                    .params(map)
                    .tag(tag)
                    .build()
                    .execute(callBack);
        }

    }

    /**
     * json请求，自动解析实体类
     * @param url
     * @param json
     * @param tag
     * @param callBack
     */
    public static void json(String url, String json, Object tag, OkCallBack<?> callBack){
        OkHttpUtils.postString()
                .url(url)
                .content(json)
                .tag(tag)
                .build()
                .execute(callBack);
    }

    /**
     * json请求，不自动解析实体类
     * @param url
     * @param json
     * @param tag
     * @param callBack
     */
    public static void json(String url, String json, Object tag, OkStringCallBack callBack){
        OkHttpUtils.postString()
                .url(url)
                .content(json)
                .tag(tag)
                .build()
                .execute(callBack);
    }

    /**
     * postFile上传单个文件，不自动解析实体类
     * @param url
     * @param file
     * @param tag
     * @param callBack
     */
    public static void file(String url, File file, Object tag, OkStringCallBack callBack){
        OkHttpUtils.postFile()
                .url(url)
                .file(file)
                .tag(tag)
                .build()
                .execute(callBack);
    }

    /**
     * postFile上传单个文件,自动解析实体类
     * @param url
     * @param file
     * @param tag
     * @param callBack
     */
    public static void file(String url, File file, Object tag, OkCallBack<?> callBack){
        OkHttpUtils.postFile()
                .url(url)
                .file(file)
                .tag(tag)
                .build()
                .execute(callBack);
    }

    /**
     * 上传多个文件，自动解析成实体类
     * @param url
     * @param key
     * @param files
     * @param tag
     * @param callBack
     */
    public static void files(String url, String key, Map<String,File> files, Object tag, OkCallBack<?> callBack){
        OkHttpUtils.post()
                .url(url)
                .files(key,files)
                .tag(tag)
                .build()
                .execute(callBack);
    }

    /**
     * 上传多个文件，不自动解析成实体类
     * @param url
     * @param key
     * @param files
     * @param tag
     * @param callBack
     */
    public static void files(String url, String key, Map<String,File> files, Object tag, OkStringCallBack callBack){
        OkHttpUtils.post()
                .url(url)
                .files(key,files)
                .tag(tag)
                .build()
                .execute(callBack);
    }

    /**
     * 文件下载
     * @param fileUrl
     * @param tag
     * @param callBack
     */
    public static void downFile(String fileUrl, Object tag, OkFileCallBack callBack){
        OkHttpUtils.get()
                .url(fileUrl)
                .tag(tag)
                .build()
                .execute(callBack);
    }


}
