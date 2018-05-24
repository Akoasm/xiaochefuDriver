package com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qf.rwxchina.xiaochefudriver.Utils.progressutils.LoadDialog;
import com.qf.rwxchina.xiaochefudriver.Utils.toastutils.ToastUtil;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/14.
 */

public abstract class OkCallBack<T> extends Callback<T> {
    Class mClass;
    private Context mC;
    private LoadDialog loadDialog;
    public static Gson mGson=new Gson();

    public OkCallBack(Class mClass, Context mC) {
        this.mClass = mClass;
        this.mC = mC;
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        T t=null;
        try {
            //解析json，返回bean对象
            t = (T) mGson.fromJson(response.body().string(), mClass);
        }catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        ToastUtil.showShort(e.toString());
        myError(call,e,id);
    }

    @Override
    public void onResponse(T response, int id) {
        myResponse(response,id);
    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
        if (loadDialog==null){
            loadDialog=new LoadDialog(mC);
        }
        if (loadDialog.isShowing()) {
            loadDialog.dismiss();
        }

    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        if (loadDialog==null){
            loadDialog=new LoadDialog(mC);
        }

        if ( !loadDialog.isShowing()) {
            loadDialog.show();
        }
    }

    public abstract void myError(Call call, Exception e, int id);
    public abstract void myResponse(T response, int id);
}
