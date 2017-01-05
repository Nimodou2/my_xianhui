package com.maibo.lvyongsheng.xianhui;

import android.os.Bundle;

import butterknife.Bind;
import cn.qqtheme.framework.widget.WheelView;

/**
 * Created by LYS on 2017/1/5.
 */

public class selectDataActivity extends BaseActivity {
    @Bind(R.id.wheelview_single)
    WheelView wheelview_single;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_data);

    }
}
