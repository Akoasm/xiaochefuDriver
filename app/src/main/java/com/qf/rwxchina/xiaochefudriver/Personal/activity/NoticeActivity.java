package com.qf.rwxchina.xiaochefudriver.Personal.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.orhanobut.logger.Logger;
import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.MyApplication;
import com.qf.rwxchina.xiaochefudriver.Personal.SwipeListView;
import com.qf.rwxchina.xiaochefudriver.Personal.adapter.NoticeAdapter;
import com.qf.rwxchina.xiaochefudriver.Personal.entity.Notice;
import com.qf.rwxchina.xiaochefudriver.R;
import com.qf.rwxchina.xiaochefudriver.Utils.logutils.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okmanager.OkManagerCallback;

/**
 * Created by Administrator on 2016/9/18.
 * 公告
 */
public class NoticeActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView gonggao_back;
    //    LinearLayout no_baodan,no_chongzhi,hupd;
//    Intent intent;
//    private ListView listView;
    private SwipeListView listView;
    private NoticeAdapter noticeAdapter;
    private int page = 1;
    private SharedPreferences sp;
    private String driverid;
    private ProgressDialog m_progressDlg;
    private List<Notice> notices;
    private SpringView springView;
    private String isRead;
    private Notice notice;
    private int width;
    private String deleteID;//被删除公告的id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);
        MyApplication.getInstance().addActivity(this);
        init();
//        getList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onStart() {
        super.onStart();
        page = 1;
        notices = new ArrayList<Notice>();
        getList();
    }

    public void init() {
        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        driverid = sp.getString("uid", "");
        Log.e("hrr", driverid);
        WindowManager wm = (WindowManager) NoticeActivity.this.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        m_progressDlg = new ProgressDialog(this);
        notices = new ArrayList<Notice>();
        gonggao_back = (ImageView) findViewById(R.id.gonggao_back);
        listView = (SwipeListView) findViewById(R.id.notice_listView);
        listView.setRightViewWidth(width / 5);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NoticeActivity.this, NoticeH5Activity.class);
                intent.putExtra("driverId", driverid);
                intent.putExtra("msgid", notices.get(i).getMsgid());
                intent.putExtra("title", notices.get(i).getTitle());
                startActivity(intent);
                EventBus.getDefault().post("getselectmsgstauts");
            }
        });
//        noticeAdapter=new NoticeAdapter(this);
        noticeAdapter = new NoticeAdapter(NoticeActivity.this, width / 5, new NoticeAdapter.IOnItemRightClickListener() {
            @Override
            public void onRightClick(View v, int position) {
                Log.e("notice_right","zoule1?");
                delete(driverid,notices.get(position).getMsgid());
                Log.e("notice_right","driverid+"+driverid);
            }
        });
        listView.setAdapter(noticeAdapter);
        springView = (SpringView) findViewById(R.id.notice_springView);
        springView.setType(SpringView.Type.FOLLOW);
        springView.setHeader(new DefaultHeader(NoticeActivity.this));
        springView.setFooter(new DefaultFooter(NoticeActivity.this));
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                notices = new ArrayList<Notice>();
                page = 1;
                getList();
            }

            @Override
            public void onLoadmore() {
                getList();
            }
        });
        gonggao_back.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gonggao_back:
                finish();
                break;
        }
    }

    //获取公告列表
    private void getList() {
        m_progressDlg.show();
        OkHttpUtils.post()
                .url(HttpPath.MESSAGELIST)
                .addParams("driverID", driverid)
                .addParams("page", page + "")
                .addParams("pageSize", "20")
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        m_progressDlg.dismiss();
                        springView.onFinishFreshAndLoad();
                        Toast.makeText(NoticeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        springView.onFinishFreshAndLoad();
                        page++;
                        m_progressDlg.dismiss();
                        Logger.t("NoticeActivity").e(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject state = jsonObject.getJSONObject("state");
                            String code = state.optString("code");
                            if ("0".equals(code)) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = (JSONObject) data.get(i);
                                    notice = new Notice();
                                    notice.setMsgid(object.optString("msgid"));
                                    notice.setCreatetime(object.optString("createtime"));
                                    notice.setIsread(object.optString("isread"));
                                    notice.setTitle(object.optString("title"));
                                    notices.add(notice);
                                }
                                noticeAdapter = new NoticeAdapter(NoticeActivity.this, width / 5, new NoticeAdapter.IOnItemRightClickListener() {
                                        @Override
                                        public void onRightClick(View v, int position) {
                                            Log.e("notice_right","zoule1?");
                                            delete(driverid,notices.get(position).getMsgid());
                                            Log.e("notice_right","driverid+"+driverid);
                                        }
                                    });
//                                }
                                listView.setAdapter(noticeAdapter);
                                noticeAdapter.setNotices(notices);
                                noticeAdapter.notifyDataSetChanged();
                            }else {
                                String msg = state.optString("msg");
                                Toast.makeText(NoticeActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 删除公告方法
     * @param driver_id  司机id
     * @param id  被删除公告id
     */
    public void delete(String driver_id,String id) {
        m_progressDlg.show();
        OkHttpUtils.post()
                .url(HttpPath.MESSAGE_DELETE)
                .addParams("driverID", driver_id)
                .addParams("id",id)
                .tag(this)
                .build()
                .execute(new OkManagerCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        m_progressDlg.dismiss();
                        Toast.makeText(NoticeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        m_progressDlg.dismiss();
                        LogUtil.e("NoticeActivity", "delete_res" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject object = jsonObject.getJSONObject("state");
                            String code = object.getString("code");
                            if (code.equals("0") || code != null) {
                                Toast.makeText(NoticeActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                                notices = new ArrayList<Notice>();
                                page=1;
                                getList();
                            } else {
                                Toast.makeText(NoticeActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }
}
