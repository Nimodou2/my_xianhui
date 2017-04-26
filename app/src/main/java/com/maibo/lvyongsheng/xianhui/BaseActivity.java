package com.maibo.lvyongsheng.xianhui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.maibo.lvyongsheng.xianhui.implement.MyProgressDialog;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.ButterKnife;

/**
 * Created by LYS on 2016/12/29.
 */

public class BaseActivity extends AutoLayoutActivity {
    //去掉状态栏的屏幕高度
    public int screenHeight;
    //去掉状态栏和标题栏后的高度
    public int viewHeight;
    ProgressDialog longDialog;
    MyProgressDialog shortDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialog();
    }

    /**
     * 初始化用于加载的dialog
     */
    private void initDialog() {
        longDialog = new ProgressDialog(this);
        longDialog.setMessage("加载中...");
        longDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        longDialog.setCancelable(true);
        longDialog.setIndeterminate(false);
        shortDialog = new MyProgressDialog(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        screenHeight = Util.getScreenHeight(this) - getStatusBarHeight();
        viewHeight = screenHeight - ((Util.getScreenHeight(this) - getStatusBarHeight()) / 35) * 2;
        ButterKnife.bind(this);
        onViewCreated();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
        onViewCreated();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
        onViewCreated();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        EventBus.getDefault().unregister(this);
    }

    protected void onViewCreated() {
    }

    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
            toast(e.getMessage());
            return false;
        } else {
            return true;
        }
    }

    protected void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }


    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    protected void startActivity(Class<?> cls, String... objs) {
        Intent intent = new Intent(this, cls);
        for (int i = 0; i < objs.length; i++) {
            intent.putExtra(objs[i], objs[++i]);
        }
        startActivity(intent);
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    //因为EventBus注册后必须存在接收函数，下面内容就是个摆设
//    public void onEvent(EmptyEvent event) {}
//    public class EmptyEvent {
//    }


    /**
     * 适配标题栏
     *
     * @param ll_head
     */
    public void adapterLitterBar(LinearLayout ll_head) {
        ViewGroup.LayoutParams params = ll_head.getLayoutParams();
        params.height = ((Util.getScreenHeight(this) - getStatusBarHeight()) / 35) * 2;
        ll_head.setLayoutParams(params);
    }

    /**
     * 设置多个view的宽和高
     *
     * @param view
     * @param height
     * @param width
     */
    public void setViewHeightAndWidth(View[] view, int[] height, int[] width) {
        for (int i = 0; i < view.length; i++) {
            ViewGroup.LayoutParams params = view[i].getLayoutParams();
            params.height = height[i];
            if (width != null)
                params.width = width[i];
            view[i].setLayoutParams(params);
        }
    }

    /**
     * 为了方便
     * 设置单个view的宽和高
     *
     * @param view
     * @param height
     * @param width
     */
    public void setSingleViewHeightAndWidth(View view, int height, int width) {

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        if (width != 0)
            params.width = width;
        view.setLayoutParams(params);

    }

    /**
     * 用于长时间加载的dialog
     */
    public void showLongDialog() {
        longDialog.show();
    }

    public void dismissLongDialog() {
        longDialog.dismiss();
    }

    /**
     * 用于短时间加载的dialog
     */
    public void showShortDialog() {
        shortDialog = new MyProgressDialog(this);
        shortDialog.show();
    }

    public void dismissShortDialog() {
        shortDialog.dismiss();
    }


}
