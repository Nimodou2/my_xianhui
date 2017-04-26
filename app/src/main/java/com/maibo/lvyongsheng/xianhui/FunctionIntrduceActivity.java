package com.maibo.lvyongsheng.xianhui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;

import butterknife.Bind;

/**
 * Created by LYS on 2017/1/11.
 */

public class FunctionIntrduceActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.back)
    TextView back;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_intrduce);
        CloseAllActivity.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        //adapterLitterBar(ll_head);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
