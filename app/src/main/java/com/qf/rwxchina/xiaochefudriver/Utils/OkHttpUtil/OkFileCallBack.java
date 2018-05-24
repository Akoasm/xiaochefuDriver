package com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil;

import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/7/19.
 */

public class OkFileCallBack extends FileCallBack {
    //TODO 等待完善，自定义文件下载器
    //destFileDir 目标文件存储的文件夹路径
    //destFileName 目标文件存储的文件名
    public OkFileCallBack(String destFileDir, String destFileName) {
        super(destFileDir, destFileName);
    }

    @Override
    public void inProgress(float progress, long total, int id) {
        super.inProgress(progress, total, id);
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    @Override
    public void onResponse(File response, int id) {

    }
}
