package com.maibo.lvyongsheng.xianhui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.jcodecraeer.xrecyclerview.ArrowRefreshHeader;
import com.maibo.lvyongsheng.xianhui.R;

/**
 * Created by LYS on 2017/3/9.
 */

public class MyRefreshHeadView extends ArrowRefreshHeader {
    public MyRefreshHeadView(Context context) {
        super(context);
        initView();
    }

    public MyRefreshHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        this.setBackgroundColor(getResources().getColor(R.color.weixin_lianxiren_gray));
        setPadding(0,0,0,10);
    }
}
