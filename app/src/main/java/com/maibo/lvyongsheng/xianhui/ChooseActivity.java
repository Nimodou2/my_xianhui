package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by LYS on 2016/10/19.
 */
public class ChooseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_choose);
    }
    //重写此方法用来设置当点击activity外部时候，关闭此弹出框
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        finish();
        return true;
    }
    //此方法在布局文件中定义，用来保证点击弹出框内部的时候不会被关闭，如果不设置此方法则单击弹出框内部时候会导致弹出框关闭
    public void tip(View view)
    {
        Toast.makeText(this, "点击弹出框外部关闭窗口~", Toast.LENGTH_SHORT).show();
    }
}
