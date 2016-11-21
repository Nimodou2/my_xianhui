package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

/**
 * @desc 启动屏
 * Created by devilwwj on 16/1/23.
 */
public class SplashActivity extends Activity {
    SharedPreferences  sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查网络连接状态
        if (isNetworkAvailable(this)){
            //网络连接正常
            //判断是否登录成功
            sp=getSharedPreferences("baseDate",MODE_PRIVATE);
            int isSuccess=sp.getInt("success",0);
            // 如果不是第一次启动app，则正常显示启动屏
            if (isSuccess==0){
                setContentView(R.layout.activity_splash);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        enterHomeActivity();
                    }
                }, 2000);
            }else{
                startActivity(new Intent(this,LoginActivity.class));
                finish();
            }

        }else{
            setContentView(R.layout.activity_splash);
            App.showToast(getApplicationContext(),"网络断开连接！");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    finish();
                }
            }, 2000);

        }

    }

    /**
     * 判断网络连接状态
     */
    private void enterHomeActivity() {
        Intent intent = new Intent(this, WelcomeGuideActivity.class);
        startActivity(intent);
        finish();
    }

    public static boolean isNetworkAvailable(final Context context) {
        boolean hasWifoCon = false;
        boolean hasMobileCon = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfos = cm.getAllNetworkInfo();
        for (NetworkInfo net : netInfos) {

            String type = net.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                if (net.isConnected()) {
                    hasWifoCon = true;
                }
            }

            if (type.equalsIgnoreCase("MOBILE")) {
                if (net.isConnected()) {
                    hasMobileCon = true;
                }
            }
        }
        return hasWifoCon || hasMobileCon;

    }
}
