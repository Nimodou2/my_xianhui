package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.os.Bundle;

import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;

/**
 * Created by LYS on 2016/9/30.
 */
public class DetailMessageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);
        CloseAllActivity.getScreenManager().pushActivity(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
