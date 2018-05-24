package com.qf.rwxchina.xiaochefudriver.Personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.R;

/**
 * Created by Administrator on 2017/7/7.
 * 详情列表
 */

public class XiangQingLieBiaoActivity extends AppCompatActivity{
    ImageView xq_back;
    TextView qibujia,qibugongli,cc_qibugonglishu,cc_qibugonglifeiyong,dengdaishijian,cc_dengdaishijian,cc_dengdaishijianfeiyong,sijikoulv;
    String qibujia1,qibugongli1,cc_qibugonglishu1,cc_qibugonglifeiyong1,dengdaishijian1,cc_dengdaishijian1,cc_dengdaishijianfeiyong1,sijikoulv1,fantype1,type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xiangqingleibiao);
        qibujia= (TextView)findViewById(R.id.qibujia);
        qibugongli= (TextView)findViewById(R.id.qibugongli);
        cc_qibugonglishu= (TextView)findViewById(R.id.cc_qibugonglishu);
        cc_qibugonglifeiyong= (TextView)findViewById(R.id.cc_qibugonglifeiyong);
        dengdaishijian= (TextView)findViewById(R.id.dengdaishijian);
        cc_dengdaishijian= (TextView) findViewById(R.id.cc_dengdaishijian);
        cc_dengdaishijianfeiyong= (TextView) findViewById(R.id.cc_dengdaishijianfeiyong);
        sijikoulv= (TextView) findViewById(R.id.sijikoulv);
        xq_back= (ImageView) findViewById(R.id.xq_back);
        xq_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getdate();
    }

    public  void getdate() {
        qibujia1=getIntent().getStringExtra("qibujia1");
        qibugongli1=getIntent().getStringExtra("qibugongli1");
        cc_qibugonglishu1=getIntent().getStringExtra("cc_qibugonglishu1");
        cc_qibugonglifeiyong1=getIntent().getStringExtra("cc_qibugonglifeiyong1");
        dengdaishijian1=getIntent().getStringExtra("dengdaishijian1");
        cc_dengdaishijian1=getIntent().getStringExtra("cc_dengdaishijian1");
        cc_dengdaishijianfeiyong1=getIntent().getStringExtra("cc_dengdaishijianfeiyong1");
        fantype1=getIntent().getStringExtra("fantype");
        type=getIntent().getStringExtra("type");//标识符0：%  1:元
        qibujia.setText(qibujia1+"元");
        qibugongli.setText(qibugongli1+"公里");
        cc_qibugonglishu.setText(cc_qibugonglishu1+"公里");
        cc_qibugonglifeiyong.setText(cc_qibugonglifeiyong1+"元");
        dengdaishijian.setText(dengdaishijian1+"分钟");
        cc_dengdaishijian.setText(cc_dengdaishijian1+"分钟");
        cc_dengdaishijianfeiyong.setText(cc_dengdaishijianfeiyong1+"分钟");

        if(type.equals("0"))
        {
            sijikoulv.setText(fantype1+"%");
        }else{
            sijikoulv.setText(fantype1+"元");
        }

    }
}
