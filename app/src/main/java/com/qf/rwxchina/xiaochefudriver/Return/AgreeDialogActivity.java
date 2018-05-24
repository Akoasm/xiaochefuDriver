package com.qf.rwxchina.xiaochefudriver.Return;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

/**
 * 同意结伴
 */
public class AgreeDialogActivity extends Activity {
    private ImageView mClose;
    private TextView mPhone;
    private LinearLayout mCall;
    final public static int REQUEST_CODE_ASK_CALL_PHONE = 123;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_agree_dialog);
        MyApplication.getInstance().addActivity(this);
        init();
    }

    private void init() {
        mClose = (ImageView) findViewById(R.id.activity_return_dialong_close);
        mPhone = (TextView) findViewById(R.id.activity_return_dialong_phone);
        mCall = (LinearLayout) findViewById(R.id.activity_return_dialong_call);

        mPhone.setText(MyApplication.phone);

    }

    public void close(View view) {
        finish();
    }

    //拨打电话
    public void call(View view) {

            if (Build.VERSION.SDK_INT >= 23) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
                if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getParent(),new String[]{Manifest.permission.CALL_PHONE},REQUEST_CODE_ASK_CALL_PHONE);
                    return;
                }else{
                    callDirectly(MyApplication.phone);
                }
            } else {
                callDirectly(MyApplication.phone);
            }

    }

    private void callDirectly(String mobile){
        intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

}
