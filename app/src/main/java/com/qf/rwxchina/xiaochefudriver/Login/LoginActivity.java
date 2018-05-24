package com.qf.rwxchina.xiaochefudriver.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.Home.MainActivity;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.MapHome.MapFragment;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.ExampleUtil;
import com.qf.rwxchina.xiaochefudriver.Utils.Network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText mPhone;
    private TextView mGetCode;
    private EditText mCode;
    private CheckBox mCheck;
    private Button mLogin;
    private TimeCount time;
    private SharedPreferences sp;
    String p,c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyApplication.getInstance().addActivity(this);
        init();
        setOnClcik();
    }

    private void setOnClcik() {
        mGetCode.setOnClickListener(this);
        mLogin.setOnClickListener(this);
    }
    private void init() {
        time = new TimeCount(60000, 1000);
        mPhone = (EditText) findViewById(R.id.activity_login_phone);
        mGetCode = (TextView) findViewById(R.id.activity_login_getCode);
        mCode = (EditText) findViewById(R.id.activity_login_code);
        mCheck = (CheckBox) findViewById(R.id.activity_login_checkbox);
        mLogin = (Button) findViewById(R.id.activity_login_login);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_login_getCode:
                p= mPhone.getText().toString();
                if(p.equals(""))
                {
                    Toast.makeText(LoginActivity.this,"请输入手机号", Toast.LENGTH_SHORT).show();
                }else
                {
                    // 开始计时
              time.start();
                    Code();
                }

                break;
            case R.id.activity_login_login:

                p=mPhone.getText().toString();
                c=mCode.getText().toString();

                if(p.equals("")||c.equals(""))
                {
                    Toast.makeText(LoginActivity.this,"请填写完整信息", Toast.LENGTH_SHORT).show();
                }else
                {
                    LOGIN();
                }
                break;
        }
    }
    /**
     * 获取验证码
     */
    private void Code() {

        if (Network.HttpTest(LoginActivity.this))
        {
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.LOGIN_CODE).addParam("phone",p).build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);

                                            HashMap<String, String> list= AnalyticalJSON.getHashMap(map.get("state"));


                                        } else {
                                            Toast.makeText(LoginActivity.this,"服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }
                            });
        }

    }

    /**
     * 登陆
     */
    public void LOGIN() {
        String mobilemodel= Build.MANUFACTURER+"-"+Build.MODEL;//手机厂商+手机型号
        String systemmodel=android.os.Build.VERSION.RELEASE;//系统版本
        String appversion = "";//系统版本
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo= packageManager.getPackageInfo("com.qf.rwxchina.xiaochefudriver", 0);
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        if (packageInfo!=null){
            appversion = packageInfo.versionName;
        }
        Log.e("hrr",mobilemodel+"   "+systemmodel+"  "+appversion);
        Log.i("login","登录接口="+HttpPath.LOGIN_PHTH+"&phone="+p+"&verift="+c);
        if (Network.HttpTest(LoginActivity.this))
        {
            OkHttpUtil.Builder()
                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
                    .setConnectTimeout(25).build(this)
                    .doPostAsync(
                            HttpInfo.Builder().setUrl(HttpPath.LOGIN_PHTH)
                                    .addParam("phone",p)
                                    .addParam("verify",c)
                                    .addParam("mobilemodel",mobilemodel)//手机型号
                                    .addParam("systemmodel",systemmodel)//系统型号
                                    .addParam("mobiletype","Android")//手机类别
                                    .addParam("appversion",appversion)//系统版本
                                    .build(),
                            new CallbackOk() {
                                @Override
                                public void onResponse(HttpInfo info) throws IOException {
                                    if (info.isSuccessful()) {
                                        //获取到数据
                                        String result = info.getRetDetail();
                                        Log.e("wh",result);

                                        if (result != null) {
                                            //将得到的json数据返回给HashMap
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                            HashMap<String, String> list_msg= AnalyticalJSON.getHashMap(map.get("state"));
                                            //登陆成功
                                            if(list_msg.get("code").equals("0")) {
                                                HashMap<String, String> list= AnalyticalJSON.getHashMap(map.get("data"));
                                                sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                                                // 写入
                                                SharedPreferences.Editor editor = sp.edit();
                                                //判断是否登录，登录过后改为true
                                                editor.putBoolean("isLogin", true);
                                                //用户ID
                                                editor.putString("uid", list.get("driverid"));
                                                //用户昵称
                                                editor.putString("name", list.get("name"));
                                                //头像
                                                editor.putString("head_image", list.get("head_image"));
                                                //手机号
                                                editor.putString("phone", list.get("phone"));
                                                //代驾次数
                                                editor.putString("agentsum", list.get("agentsum"));
                                                //评星次数
                                                editor.putString("avglevel", list.get("avglevel"));
                                                //服务状态
                                                editor.putInt("work_status",1);
                                                //提交
                                                editor.commit();

                                                MyApplication app= (MyApplication) getApplication();
                                                app.setEntityName(list.get("driverid"));

                                                MapFragment.isLogin = true;
                                                setAlias();
                                                startService(MyApplication.service);
                                                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }else
                                            {


                                                Toast.makeText(LoginActivity.this,list_msg.get("msg"), Toast.LENGTH_SHORT).show();
                                            }


                                        } else {

                                            Toast.makeText(LoginActivity.this,"服务器连接失败", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                }
                            });
        }


    }

    /**
     * 验证码倒计时
     */

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        // 计时过程
        @Override
        public void onTick(long millisUntilFinished) {
            mGetCode.setClickable(false);//防止重复点击
            mGetCode.setText(millisUntilFinished / 1000 + "s");

        }


        //计时完毕
        @Override
        public void onFinish() {
            mGetCode.setText("获取验证码");
            mGetCode.setClickable(true);
        }
    }

    //设置用户的别名
    private void setAlias() {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", "");
        String alias = uid;
        Log.e("wh","需要设计的司机别名="+alias);
        if (TextUtils.isEmpty(alias)){
            return;
        }
        if (!ExampleUtil.isValidTagAndAlias(alias)){
            return;
        }
        handler.sendMessage(handler.obtainMessage(100,alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code){
                case 0:
                    Log.e("kunlun","别名设置成功,别名="+alias);
                    break;
                case 6002:
                    Log.e("kunlun","别名设置失败");
                    handler.sendMessageDelayed(handler.obtainMessage(100,alias),1000*60);
                    break;
            }
        }
    };
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    JPushInterface.setAliasAndTags(getApplicationContext(),(String) msg.obj,null,mAliasCallback);
                    break;
                default:
                    Log.e("kunlun","Unhandled msg - " + msg.what);
            }
        }
    };

}
