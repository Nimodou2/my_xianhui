package com.maibo.lvyongsheng.xianhui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;

import butterknife.Bind;

/**
 * Created by LYS on 2017/1/11.
 */

public class IntrduceAppActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tv_app_version)
    TextView tv_app_version;
    @Bind(R.id.tv_intrduce_function)
    TextView tv_intrduce_function;
    @Bind(R.id.tv_system_nitification)
    TextView tv_system_nitofication;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.back)
    TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrduce_app);
        CloseAllActivity.getScreenManager().pushActivity(this);

        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        //adapterLitterBar(ll_head);
        back.setOnClickListener(this);
        tv_intrduce_function.setOnClickListener(this);
        tv_system_nitofication.setOnClickListener(this);
        String version = getVersion();
        if (!TextUtils.isEmpty(version)) {
            tv_app_version.setText(version);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_intrduce_function:
                //跳转到功能介绍界面
                startActivity(new Intent(this, FunctionIntrduceActivity.class));
                break;
            case R.id.tv_system_nitification:
                //跳转到系统通知界面
                startActivity(new Intent(this, SystemNotificationActivity.class));
                break;
        }
    }

    private String getVersion() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pf = pm.getPackageInfo(getPackageName(), 0);
            String version = pf.versionName;
            return "闲惠企业版 " + version;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
