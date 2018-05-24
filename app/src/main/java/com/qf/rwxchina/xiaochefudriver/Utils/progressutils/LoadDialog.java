package com.qf.rwxchina.xiaochefudriver.Utils.progressutils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.qf.rwxchina.xiaochefudriver.R;

/**
 * Created by Administrator on 2017/7/13.
 * 加载中弹出框工具类
 */

public class LoadDialog extends Dialog {
    private Context context;//上下文环境
    private TextView my_hint_tv;//设置文字
    private ImageView progress_bkg_img;//加载图片
    private String content;//文字内容

    public LoadDialog(Context context){
        super(context);
        this.context=context;
        this.content="正在加载，请稍等……";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hint_progress);
        initView();
    }

    private void initView(){
        my_hint_tv = (TextView)findViewById(R.id.my_hint_tv);
        my_hint_tv.setText(content);
        progress_bkg_img = (ImageView)findViewById(R.id.progress_bkg_img);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
        progress_bkg_img.startAnimation(animation);
    }
}
