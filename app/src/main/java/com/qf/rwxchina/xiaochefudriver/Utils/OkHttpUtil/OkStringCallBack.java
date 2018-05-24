package com.qf.rwxchina.xiaochefudriver.Utils.OkHttpUtil;

import android.content.Context;

import com.qf.rwxchina.xiaochefudriver.Utils.progressutils.LoadDialog;
import com.qf.rwxchina.xiaochefudriver.Utils.toastutils.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/7/14.
 *
 */

public abstract class OkStringCallBack extends StringCallback {
    private LoadDialog loadDialog;
    private Context mC;

    public OkStringCallBack(Context mC) {
        this.mC = mC;
    }
    public OkStringCallBack() {
    }

    public OkStringCallBack(LoadDialog loadDialog, Context mC) {
        this.loadDialog = loadDialog;
        this.mC = mC;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        ToastUtil.showShort(e.toString());
        myError(call,e,id);
    }

    @Override
    public void onResponse(String response, int id) {
        myResponse(response,id);
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        if (mC!=null){
            if (loadDialog==null){
                loadDialog=new LoadDialog(mC);
            }
            if ( !loadDialog.isShowing()) {
                loadDialog.show();
            }
        }

    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
        if (mC!=null){
            if (loadDialog==null){
                loadDialog=new LoadDialog(mC);
            }
            if (loadDialog.isShowing()) {
                loadDialog.dismiss();
            }
        }

    }

    public abstract void myError(Call call, Exception e, int id);
    public abstract void myResponse(String response, int id);
}
