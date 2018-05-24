package com.qf.rwxchina.xiaochefudriver.Personal.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheLevel;
import com.okhttplib.callback.CallbackOk;
import com.orhanobut.logger.Logger;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.Home.MainActivity;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.Login.LoginActivity;

import com.qf.rwxchina.xiaochefudriver.MapHome.MapFragment;
import com.qf.rwxchina.xiaochefudriver.Personal.About_xiaochefu;
import com.qf.rwxchina.xiaochefudriver.Personal.AccountInfoActivity;
import com.qf.rwxchina.xiaochefudriver.Personal.Cooperation_process;
import com.qf.rwxchina.xiaochefudriver.Personal.Driver_details;
import com.qf.rwxchina.xiaochefudriver.Personal.My_account;
import com.qf.rwxchina.xiaochefudriver.Personal.activity.NoticeActivity;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Return.ComeBackActivity;
import com.qf.rwxchina.xiaochefudriver.Utils.AnalyticalJSON;
import com.qf.rwxchina.xiaochefudriver.Utils.HttpInvoker;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import org.apache.http.HttpResponse;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import okhttp3.Call;
import q.rorbin.badgeview.QBadgeView;

/**
 * 个人中心
 */
public class PersonalFragment extends Fragment implements View.OnClickListener{

    LinearLayout guanyuxiaochefu,my_zhanghu,gonggao,liucheng,lin_siji,lin_name,ycqd,jiebanfancheng,banben,shoufei;
    TextView weidenglu;
    ImageView g_img;
    TextView  g_name,g_daijia;
    private SharedPreferences sp;
    Button tuichu;
    //判断是否登录 false：未登录/ true：登录
    Boolean isLogin = false;
    String  uid,img,na,dj,agentsum,avglevel,driverid;
    RatingBar pi;
    Dialog dialog;
    int workstate;
    private TextView versonNmae;//版本
    private TextView update_verson;//版本更新提示
    String m_newVerName; // 最新版的版本名
    String m_appNameStr; // 下载到本地要给这个APP命的名字
    Handler m_mainHandler;
    ProgressDialog m_progressDlg;
    private ProgressDialog selet;
    private boolean ifUpdate=false;
    private String down=null;
    private TextView msg_num;
    private QBadgeView qBadgeView;//显示公告未读条数
    protected boolean isViewInitiated;//是否加载布局
    protected boolean isVisibleToUser;//视图是否可见
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated=true;
        into();
        sp = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        //登录状态
        isLogin = sp.getBoolean("isLogin", false);
        driverid = sp.getString("uid","");
        if (isLogin) {
            lin_name.setVisibility(View.VISIBLE);
            weidenglu.setVisibility(View.GONE);
            //用户id
            uid = sp.getString("uid", "");
           //头像
            img=sp.getString("head_image","");
           //名称
            na=sp.getString("name","");
            //代驾次数
            agentsum=sp.getString("agentsum","");
            //评星等级
            avglevel=sp.getString("avglevel","");
            workstate = sp.getInt("work_status",0);
           //头像
            if(img.equals("")){
                g_img.setImageResource(R.drawable.icon_account);
            }else{
                Picasso.with(getActivity()).load(img).error(R.drawable.icon_account).into(g_img);
            }

            //名称
            if(na.equals("")){
                g_name.setText("小车夫");
            }else{
                g_name.setText(na);
            }

            //代驾次数
            if(agentsum.equals("")){
                g_daijia.setText("未使用代驾");
            }else{
                g_daijia.setText("代驾次数:"+agentsum+"次");
            }

            //评星等级
            if(avglevel.equals("")){
                pi.setRating(0.0f);
            }else{

                pi.setRating(Float.parseFloat(avglevel));
            }
            getselectmsgstauts();

        } else {
            lin_name.setVisibility(View.GONE);
            weidenglu.setVisibility(View.VISIBLE);
            weidenglu.setText("未登录");
        }
        //注册eventbus
        EventBus.getDefault().register(this);


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        this.isVisibleToUser=isVisibleToUser;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.t("hrr").e("onHiddenChanged="+hidden);

        if (!hidden){
            getselectmsgstauts();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化控件
     */
    public void into() {
        qBadgeView=new QBadgeView(getActivity());
        m_mainHandler = new Handler();
        selet=new ProgressDialog(getActivity());
        m_progressDlg = new ProgressDialog(getActivity());
        m_progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
        m_progressDlg.setIndeterminate(false);
        msg_num= (TextView) getView().findViewById(R.id.msg_num);
        versonNmae= (TextView) getView().findViewById(R.id.versonName);
        update_verson= (TextView) getView().findViewById(R.id.update_verson);
        shoufei= (LinearLayout) getView().findViewById(R.id.my_shoufei);
        guanyuxiaochefu= (LinearLayout) getView().findViewById(R.id.guanyuxiaochefu);
        my_zhanghu= (LinearLayout) getView().findViewById(R.id.my_zhanghu);
        gonggao= (LinearLayout) getView().findViewById(R.id.gonggao);
        liucheng= (LinearLayout) getView().findViewById(R.id.liucheng);
        lin_siji= (LinearLayout) getView().findViewById(R.id.lin_siji);
        lin_name= (LinearLayout) getView().findViewById(R.id.lin_name);
        weidenglu= (TextView) getView().findViewById(R.id.weidenglu);
        ycqd= (LinearLayout) getView().findViewById(R.id.ycqd);
        tuichu= (Button) getView().findViewById(R.id.tuichu);
        g_img= (ImageView) getView().findViewById(R.id.g_img);
        g_name= (TextView) getView().findViewById(R.id.g_name);
        g_daijia= (TextView) getView().findViewById(R.id.g_daijia);
        pi= (RatingBar) getView().findViewById(R.id.pi);
        tuichu= (Button) getView().findViewById(R.id.tuichu);
        jiebanfancheng= (LinearLayout) getView().findViewById(R.id.jiebanfancheng);
        banben= (LinearLayout) getView().findViewById(R.id.banben);
        shoufei.setOnClickListener(this);
        tuichu.setOnClickListener(this);
        guanyuxiaochefu.setOnClickListener(this);
        my_zhanghu.setOnClickListener(this);
        gonggao.setOnClickListener(this);
        liucheng.setOnClickListener(this);
        lin_siji.setOnClickListener(this);
        tuichu.setOnClickListener(this);
        ycqd.setOnClickListener(this);
        jiebanfancheng.setOnClickListener(this);
        banben.setOnClickListener(this);
        versonNmae.setText("当前版本：V"+getVerName(getActivity()));
        ifUpdate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
           //关于小车夫
            case R.id.guanyuxiaochefu:
                Intent i=new Intent(getActivity(),About_xiaochefu.class);
                startActivity(i);
                break;
            case R.id.my_shoufei:
                Intent shoufei=new Intent(getActivity(),AccountInfoActivity.class);
                startActivity(shoufei);
                break;
            //我的账户
            case R.id.my_zhanghu:

                if(isLogin){

                    Intent intent=new Intent(getActivity(),My_account.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                }

                break;

            //公告
            case R.id.gonggao:
                Log.e("hrr","跳转至公告");
                Intent inten=new Intent(getActivity(),NoticeActivity.class);
                startActivity(inten);
                break;

            //合作流程
            case R.id.liucheng:
                Intent in=new Intent(getActivity(),Cooperation_process.class);
                startActivity(in);
                break;
            //司机详情
            case  R.id.lin_siji:
                if(isLogin) {
                    Intent sj=new Intent(getActivity(),Driver_details.class);
                    startActivity(sj);
                }else {
                    Intent it=new Intent(getActivity(), LoginActivity.class);
                    startActivity(it);
                }
                break;

            case  R.id.tuichu:
                dialog = new Dialog(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);

                View view_exi = LayoutInflater.from(getActivity()).inflate(
                        R.layout.dialog_judge, null);
                TextView title_exit = (TextView) view_exi.findViewById(R.id.title);
                title_exit.setText("是否退出账号?");
                view_exi.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                     if(workstate==2) {
                         Toast.makeText(getActivity(), "请完成当前订单", Toast.LENGTH_SHORT).show();
                         dialog.dismiss();
                     }else {
                         getActivity().stopService(MyApplication.service);
                         // 写入
                         SharedPreferences.Editor editor = sp.edit();

                         editor.putBoolean("isLogin",false);
//                         editor.putInt("work_status",0);
                         editor.putInt("work_status",1);
                         //提交
                         editor.commit();

                         MapFragment.isLogin = false;
                         MainActivity activity = (MainActivity) getActivity();
                         activity.offwork();

                         Intent intent1=new Intent(getActivity(),LoginActivity.class);
                         startActivity(intent1);
                         dialog.dismiss();
                     }
                    }
                });
                view_exi.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(view_exi);
                dialog.show();
                break;
            //异常取单
            case R.id.ycqd:

            if(isLogin){
                yichang();
            }else{
                Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
            }

                break;
            //结伴返程
            case R.id.jiebanfancheng:
                Intent intent1=new Intent(getActivity(),ComeBackActivity.class);
                startActivity(intent1);
                break;
            //版本更新
            case R.id.banben:
                if (ifUpdate){
                    doNewVersionUpdate(down);
                }else {
                    Toast.makeText(getActivity(), "已是最新版本", Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }


    /**
     * 异常取单
     */
    public void yichang(){
        OkHttpUtil.Builder()
            .setCacheLevel(CacheLevel.FIRST_LEVEL)
            .setConnectTimeout(25).build(this)
            .doPostAsync(
                    HttpInfo.Builder().setUrl(HttpPath.yc)
                            .addParam("driverID",driverid)
                            .build(),
                    new CallbackOk() {
                        @Override
                        public void onResponse(HttpInfo info) throws IOException {
                            if (info.isSuccessful()) {
                                //获取到数据
                                String result = info.getRetDetail();
                                if (result != null) {
                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(result);
                                    HashMap<String, String> list_msg = AnalyticalJSON.getHashMap(map.get("state"));
                                    if(list_msg.get("code").equals("0")) {
                                        Toast.makeText(getActivity(), list_msg.get("msg"), Toast.LENGTH_SHORT).show();
                                        SharedPreferences sp = getActivity().getSharedPreferences("userInfo",getActivity(). MODE_PRIVATE);
                                        // 写入
                                        SharedPreferences.Editor editor = sp.edit();
                                        //更改上班状态
                                        editor.putInt("work_status", 1);
                                        //提交
                                        editor.commit();
                                        MyApplication.orderState = 0;

                                    }else {
                                        Toast.makeText(getActivity(), list_msg.get("msg"),Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

    }




    @Override
    public void onResume() {
        super.onResume();
//        Logger.t("hrr").e("onResume");
        getselectmsgstauts();

        //登录状态
        isLogin = sp.getBoolean("isLogin", false);
        if (isLogin) {
            lin_name.setVisibility(View.VISIBLE);
            weidenglu.setVisibility(View.GONE);
            //用户id
            uid = sp.getString("uid", "");
            //头像
            img=sp.getString("head_image","");
            //名称
            na=sp.getString("name","");
            //代驾次数
            agentsum=sp.getString("agentsum","");
            //评星等级
            avglevel=sp.getString("avglevel","");
            workstate = sp.getInt("work_status",0);



            //头像
            if(img.equals("")){
                g_img.setImageResource(R.drawable.icon_account);
            }else{
                Picasso.with(getActivity()).load(img).error(R.drawable.icon_account).into(g_img);
            }

            //名称
            if(na.equals("")){
                g_name.setText("小车夫");
            }else
            {
                g_name.setText(na);
            }

            //代驾次数
            if(agentsum.equals("")){
                g_daijia.setText("未使用代驾");
            }else
            {
                g_daijia.setText("代驾次数:"+agentsum+"次");
            }

            //评星等级
            if(avglevel.equals("")){
                pi.setRating(0.0f);
            }else{

                pi.setRating(Float.parseFloat(avglevel));
            }


        } else {
            lin_name.setVisibility(View.GONE);
            weidenglu.setVisibility(View.VISIBLE);
            weidenglu.setText("未登录");
        }
//        Intent intent=getActivity().getIntent();
//        String i=intent.getStringExtra("i");
//        if(i.equals("0"))
//        {
//            yichang();
//        }

    }
    //向服务器传递下班
    public void HttpGetOffWork() {
        OkHttpUtil.Builder()
                .setCacheLevel(CacheLevel.FIRST_LEVEL)
                .setConnectTimeout(25).build(this)
                .doPostAsync(
                        HttpInfo.Builder().setUrl(HttpPath.WORK_PATH)
                                .addParam("driverid",driverid)
                                .addParam("state","2")
                                .build(),
                        new CallbackOk() {
                            @Override
                            public void onResponse(HttpInfo info) throws IOException {
                                if (info.isSuccessful()) {
                                    //获取到数据
                                    String result = info.getRetDetail();

                                    if (result != null) {
                                        Log.e("kunlun","result="+result);
                                        try {
                                            JSONObject obj1 = new JSONObject(result);
                                            JSONObject obj2 = new JSONObject(obj1.optString("state"));
                                            String code = obj2.optString("code");
                                            if ("0".equals(code)){
                                                gooffwork();
                                            }else {
                                                Toast.makeText(getActivity(), "服务器故障，请稍后再试", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
    }
    /**
     * 下班
     */
    public void gooffwork(){
        int work_state = sp.getInt("work_status",0);
        if (work_state == 1 || work_state == 0){
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("work_status",0);
            editor.commit();

        }else if (work_state == 2){
            Toast.makeText(getContext(),"您还有订单未完成，请先完成订单",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    "com.qf.rwxchina.xiaochefudriver", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg", e.getMessage());
        }
        return verName;
    }
    Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String name= (String) msg.obj;
            String vername=null;
            if (name!=null){
                try {
                    JSONObject jsonObject=new JSONObject(name);
                    m_newVerName=jsonObject.getString("etition");
                    down=jsonObject.getString("downloadurl");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (m_newVerName!=null&&down!=null){
                Log.e("hrr","vername="+m_newVerName+" getvername="+getVerName(getActivity())+"  down="+down);
                if (!m_newVerName.equals(getVerName(getActivity()))){//判断服务器apk版本号是否和本地相同
                    m_appNameStr = "XCFDriver.apk";
                    ifUpdate=true;
                    update_verson.setText("(可更新)");
                }else {
                    ifUpdate=false;
                    update_verson.setText("(不可更新)");
                }
            }

        }
    };
    /**
     * 检查是否需要更新
     */
    private void ifUpdate(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String version= HttpInvoker.HttpGetMethod(HttpPath.UPDATEURL);
                Message msg=new Message();
                msg.obj=version;
                mhandler.sendMessage(msg);
            }
        }).start();
    }
    /**
     * 告诉HANDER已经下载完成了，可以安装了
     */
    private void down() {
        m_mainHandler.post(new Runnable() {
            public void run() {
//                Log.i("lkymsg","321321");
                m_progressDlg.cancel();
                update();
            }
        });
    }

    /**
     * 安装程序
     */
    void update() {
        File file = new File(Environment.getExternalStorageDirectory()
                , m_appNameStr);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 以新压入栈
        intent.addCategory("android.intent.category.DEFAULT");
        Uri abc = Uri.fromFile(file);
        intent.setDataAndType(abc, "application/vnd.android.package-archive");
        startActivity(intent);
    }
    /**
     * 提示更新新版本
     *
     * @param
     */
    private void doNewVersionUpdate(final String loadurl) {

        String verName = getVerName(getActivity());
        String str = "当前版本：" + verName + " ,发现新版本：" + m_newVerName + " ,是否更新？";
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("软件更新")
                .setMessage(str)
                // 设置内容
                .setPositiveButton("更新",// 设置确定按钮
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                m_progressDlg.setTitle("正在下载");
                                m_progressDlg.setMessage("请稍候...");
                                downFile(loadurl); // 开始下载
                            }
                        })
                .setNegativeButton("暂不更新",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                //关闭对话框
                                dialog.dismiss();
                                // 点击"取消"按钮之后退出程序
//                                finish();
                            }
                        }).create();// 创建
        // 显示对话框
        dialog.show();
    }

    /**
     * 下载安装包
     * @param url
     */
    private void downFile(final String url) {
        m_progressDlg.show();
        new Thread() {
            public void run() {

                HttpResponse response;
                try {
                    URL url1 = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                    long length = connection.getContentLength();
//                    Log.i("hrr","length="+length);
                    m_progressDlg.setMax((int) length);// 设置进度条的最大值

                    InputStream is = (InputStream) connection.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory()
                                , m_appNameStr);
                        Log.e("hrr","路径="+file.getPath());
//                        File file = new File(
//                                getContext().getPackageResourcePath()
//                                );
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[4154];
                        int ch = -1;
                        int count = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            if (length > 0) {
                                m_progressDlg.setProgress(count);
                            }
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
//                    Log.i("lkymsg","123123");

                    down(); // 告诉HANDER已经下载完成了，可以安装了
                } catch (IOException e) {
//                    Log.i("hrr","IOException="+e.toString());
                    e.printStackTrace();
                } catch (Exception e) {
//                    Log.i("hrr","Exception="+e.toString());
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //查询司机是否有未读公告
    private void getselectmsgstauts(){
        if((driverid!=null)&&(!"".equals(driverid))){
            selet.show();
            OkHttpUtils.post()
                    .url(HttpPath.SELECTMSGSTATUS)
                    .addParams("driverID",driverid)
                    .tag(this)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            selet.dismiss();
                            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Logger.t("hrr").e(response);
                            selet.dismiss();
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                JSONObject state=jsonObject.getJSONObject("state");
                                String code=state.optString("code");
                                if ("0".equals(code)){
                                    JSONObject data=jsonObject.getJSONObject("data");
                                    String count=data.optString("count");
                                    qBadgeView.bindTarget(msg_num);
                                    qBadgeView.setBadgeTextColor(0xffffffff);
                                    qBadgeView.setBadgeBackgroundColor(0xffff0000);
                                    qBadgeView.setBadgeNumber(Integer.parseInt(count));
                                    qBadgeView.setBadgeGravity(Gravity.CENTER);
                                }else {
                                    String msg=state.optString("msg");
                                    Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(String type){
        switch (type){
            case "getselectmsgstauts":
                Logger.t("hrr").e("接收到EventBus");
//                getselectmsgstauts();
                break;
        }
    }
}
