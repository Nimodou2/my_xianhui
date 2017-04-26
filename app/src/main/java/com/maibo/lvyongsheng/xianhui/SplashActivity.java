package com.maibo.lvyongsheng.xianhui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 启动屏
 * Created by devilwwj on 16/1/23.
 */
public class SplashActivity extends Activity {
    SharedPreferences  sp;
    List<String> list;
    private boolean isShowTwo=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isShowTwo=false;
        //检查网络连接状态
        if (isNetworkAvailable(this)){
            //获取相关权限
            boolean one= ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED;
            boolean two=ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED;
            boolean three=ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED;
            if (one||two||three){
                showOpenPermissionDialog();
            }else{
                toLoginActivity();
            }


        }else{
//            setContentView(R.layout.activity_splash);
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
     * 全部都通过的情况下
     */
    private void toLoginActivity() {
        //网络连接正常
        //判断是否登录成功
        sp = getSharedPreferences("baseDate", MODE_PRIVATE);
        int isSuccess = sp.getInt("success", 0);
        // 如果不是第一次启动app，则正常显示启动屏
        if (isSuccess == 0) {
               //setContentView(R.layout.activity_splash);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    enterHomeActivity();
                }
            }, 2000);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
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

    /**
     * 获取权限
     */
    private void getPermission() {
        boolean one= ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED;
        boolean two=ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED;
        boolean three=ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
        boolean needState[]={one,two,three};
        String permission[]={Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE};
        list=new ArrayList<>();
        for (int i=0;i<needState.length;i++){
            if (needState[i]){
                list.add(permission[i]);
            }
        }
        String newPermission[]=new String[list.size()];
        for (int i=0;i<list.size();i++){
            newPermission[i]=list.get(i);
        }
        if (newPermission.length>0){
            ActivityCompat.requestPermissions(SplashActivity.this,newPermission,0);
        }
    }

    /**
     * 每个请求都会返回一个结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        switch (requestCode){
            case 0:
                int perSize=permissions.length;
                boolean ishave=false;
                int tag=0;
                for (int i=0;i<perSize;i++){
                    boolean aa=ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, list.get(i));
                    if (aa){
                        showSimpleDialog();
                        ishave=true;
                        return;
                    } else if (grantResults[i]==-1&&!aa) {
                        if (ishave){
                            showSimpleDialog();
                            return;
                        }else{
                            showSettingDialog();
                            return;
                        }
                    }
                    if (grantResults[i]==0){
                        tag++;
                    }
                }
                if (tag==grantResults.length){
                    toLoginActivity();
                }

                break;
        }
    }

    private void showOpenPermissionDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.promiss_dialog_style,null);
        Button btn= (Button) view.findViewById(R.id.btn);
        builder.setView(view);
        //设置对话框不可取消
        builder.setCancelable(false);
        final AlertDialog dialog=builder.create();
        dialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermission();
                dialog.dismiss();
            }
        });

    }
    /**
     * 提示继续打开权限对话框
     */
    private void showSimpleDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提醒");
        builder.setMessage("请您打开这些权限，否则您将无法正常使用闲惠");
        //设置对话框是可取消的
        builder.setCancelable(false);
        AlertDialog dialog=builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPermission();
                dialog.dismiss();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(5,122,240));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(5,122,240));
    }

    /**
     * 提示到设置中开启权限
     */
    private void showSettingDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提醒");
        builder.setMessage("请您到设置中开启权限，否则您将无法正常使用闲惠");
        //设置对话框是可取消的
        builder.setCancelable(false);
        AlertDialog dialog=builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSetting();
                dialog.dismiss();
                isShowTwo=true;
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.rgb(5,122,240));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(5,122,240));
    }

    /**
     *开启系统设置
     */
    private void openAppSetting(){
        Uri packageURI = Uri.parse("package:" + this.getPackageName());
        Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //再次检查权限
        if (isShowTwo){
            boolean one= ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED;
            boolean two=ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED;
            boolean three=ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED;
            if (one||two||three){
                showSimpleDialog();
                isShowTwo=false;
            }else{
                toLoginActivity();
            }
        }

    }
}
