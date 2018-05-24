package com.qf.rwxchina.xiaochefudriver.State;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qf.rwxchina.xiaochefudriver.R;

/**
 * 状态fragment2
 */
public class StateFragment2 extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_state, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

    }
}
