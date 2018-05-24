package com.qf.rwxchina.xiaochefudriver.alipay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.Home.MainActivity;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.R;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/4/19 0019.
 */
public class PayOkActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView okback;
    TextView baoxiangongsi,baoxiantime,baoxianmoney;
    String  uid;
    private SharedPreferences sp;
    //判断是否登录 false：未登录/ true：登录
    Boolean isLogin = false;
    HashMap<String, String> list_date;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_ok);
        MyApplication.getInstance().addActivity(this);
        init();
    }
    private void init(){
        sp = getApplicationContext().getSharedPreferences("userInfo",MODE_PRIVATE);
        //登录状态
        isLogin = sp.getBoolean("isLogin", false);
        if (isLogin) {
            //用户id
            uid = sp.getString("uid", "");
//            URSE();
        }
        Intent i=getIntent();
        okback= (ImageView) findViewById(R.id.pay_ok_back);
        baoxiangongsi= (TextView) findViewById(R.id.pay_baoxiangongsi);
        baoxiantime= (TextView) findViewById(R.id.pay_dingdantime);
        baoxianmoney= (TextView) findViewById(R.id.pay_money);
        if (i!=null){
            Bundle b =i.getExtras();
            if (b!=null){
                baoxiangongsi.setText(b.getString("name"));
                baoxiantime.setText(b.getString("time"));
                baoxianmoney.setText(b.getString("money"));
            }
        }
        okback.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pay_ok_back:
                Intent intent=new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }
//    /**
//     * 用户基本信息
//     */
//
//    private void URSE() {
//        if (Network.HttpTest(getApplicationContext())) {
//            OkHttpUtil.Builder()
//                    .setCacheLevel(CacheLevel.FIRST_LEVEL)
//                    .setConnectTimeout(25).build(this)
//                    .doPostAsync(
//                            HttpInfo.Builder().setUrl(HttpPaths.URSE).addParam("uid", uid).build(),
//                            new CallbackOk() {
//                                @Override
//                                public void onResponse(HttpInfo info) throws IOException {
//                                    if (info.isSuccessful()) {
//                                        //获取到数据
//                                        String result = info.getRetDetail();
//                                        Logger.t("touxiang").e(result);
//                                        if (result != null) {
//                                            //将得到的json数据返回给HashMap
//                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
//                                            HashMap<String, String> list = AnalyticalJSON.getHashMap(map.get("state"));
//                                            if (list.get("code").equals("0")) {
//                                                list_date = AnalyticalJSON.getHashMap(map.get("data"));
//                                                //用户类型
//                                                MyApplication.userType = Integer.parseInt(list_date.get("type_id"));
//                                                //当前余额
//                                                MyApplication.yue=list_date.get("balance");
//                                                //当前积分
//                                                MyApplication.jifen=list_date.get("integral");
//                                            } else {
//                                                Toast.makeText(getApplicationContext(), list.get("msg"), Toast.LENGTH_SHORT).show();
//                                            }
//                                        } else {
//                                            Toast.makeText(getApplicationContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                    }
//                                }
//                            });
//        }
//
//    }

}
