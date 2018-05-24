package com.qf.rwxchina.xiaochefudriver.Personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.Bean.HttpPath;
import com.qf.rwxchina.xiaochefudriver.R;

/**
 * Created by Administrator on 2017/7/12.
 */

public class NoticeH5Activity extends AppCompatActivity implements View.OnClickListener{
    private String url;
    private WebView webView;
    private ImageView back;
    private TextView title_tv;
    private String title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeh5);
        init();
        setWebView();
    }

    //初始化数据
    private void init(){
        Intent intent=getIntent();
        String driverId=intent.getStringExtra("driverId");
        String msgid=intent.getStringExtra("msgid");
        title=intent.getStringExtra("title");
//        String driverId="102";
//        String msgid="582";
        url=HttpPath.INFO+"?driverID="+driverId+"&msgid="+msgid;
        webView= (WebView) findViewById(R.id.activity_notice_webView);
        back= (ImageView) findViewById(R.id.noticeh5_back);
        back.setOnClickListener(this);
        title_tv= (TextView) findViewById(R.id.noticeh5_title);
        title_tv.setText(title);
    }

    //设置webview
    private void setWebView(){
        //WebView加载web资源
        webView.loadUrl(url);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.noticeh5_back:
                finish();
                break;
        }
    }
}
