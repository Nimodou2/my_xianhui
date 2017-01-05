package com.maibo.lvyongsheng.xianhui;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by LYS on 2016/9/14.
 */
public class AddTabActivity extends TabActivity implements View.OnClickListener{

    RadioGroup radiogrop;
    RadioButton btn1,btn2;
    LinearLayout ll_head;
    SharedPreferences sp;
    String apiURL;
    String token;
    int cusId;
    TextView tv_quite,tv_certain;
    int isQuit=0;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tab);

        CloseAllActivity.getScreenManager().pushActivity(this);
        dialog=new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);

        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        Intent intent3=getIntent();
        cusId=intent3.getIntExtra("customer_id",0);

        radiogrop=(RadioGroup) findViewById(R.id.rg_menu);
        btn1 = (RadioButton) findViewById(R.id.button1);
        btn2 = (RadioButton) findViewById(R.id.button2);
        ll_head= (LinearLayout) findViewById(R.id.ll_head);
        adapterLitterBar(ll_head);

        btn1.setTextColor(Color.WHITE);
        btn2.setTextColor(Color.rgb(1,122,255));

        tv_quite= (TextView) findViewById(R.id.tv_quit);
        tv_certain= (TextView) findViewById(R.id.tv_certain);
        tv_quite.setOnClickListener(this);
        tv_certain.setOnClickListener(this);

        final TabHost tab=getTabHost();
        Intent intent=getIntent();
        int cusId=intent.getIntExtra("customer_id",0);
        Intent intent1 =new Intent(this,AddProjectActivity.class);
        intent1.putExtra("customer_id",cusId);
        tab.addTab(tab.newTabSpec("first").setIndicator("ONE").setContent(intent1));
        Intent intent2 =new Intent(this,AddProductActivity.class);
        intent2.putExtra("customer_id",cusId);
        tab.addTab(tab.newTabSpec("second").setIndicator("TWO").setContent(intent2));


        radiogrop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.button1:
                        btn1.setTextColor(Color.WHITE);
                        btn2.setTextColor(Color.rgb(1,122,255));
                        tab.setCurrentTabByTag("first");
                        break;
                    case R.id.button2:
                        btn2.setTextColor(Color.WHITE);
                        btn1.setTextColor(Color.rgb(1,122,255));
                        tab.setCurrentTabByTag("second");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isQuit==1) {
            String buffer1 = sp.getString("bufferProduct", null);
            String buffer2 = sp.getString("bufferProject", null);
            String buffer_product_no = sp.getString("buffer_product_no", null);
            int tag = sp.getInt("tag", -1);
            String buffer = buffer1 + "," + buffer2;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("bufferAll", buffer);
            editor.putString("bufferProduct", null);
            editor.putString("bufferProject", null);
            editor.commit();

            //保存计划项目和产品
            if (tag == 0) {
                if (buffer_product_no != null && buffer2 != null) {
                    dialog.show();
                    String buffer_one = buffer_product_no + "," + buffer2;
                    savePlanItem(buffer_one);
                } else if (buffer_product_no != null && buffer2 == null) {
                    dialog.show();
                    savePlanItem(buffer_product_no);
                } else if (buffer_product_no == null && buffer2 != null) {
                    dialog.show();
                    savePlanItem(buffer2);
                } else {
                    App.showToast(getApplicationContext(), "保存失败");
                }
            } else if (tag == 1) {
                if (buffer1 != null && buffer2 != null) {
                    String buffer_one = buffer1 + "," + buffer2;
                    savePlanItem(buffer_one);
                } else if (buffer1 != null && buffer2 == null) {
                    savePlanItem(buffer1);
                } else if (buffer1 == null && buffer2 != null) {
                    savePlanItem(buffer2);
                } else {
                    App.showToast(getApplicationContext(), "保存失败");
                }
            }
        }
    }
    public void savePlanItem(String buffer){
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/setplanitem")
                .addParams("token", token)
                .addParams("customer_id", cusId + "")
                .addParams("ids", buffer)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        App.showToast(getApplicationContext(),"网络连接异常");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        String status = object.get("status").getAsString();
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_quit:
                isQuit=0;
                finish();
                break;
            case R.id.tv_certain:
                isQuit=1;
                finish();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }

    /**
     * 适配tab的高度
     * @param ll_head
     */
    public void adapterLitterBar(LinearLayout ll_head){
        ViewGroup.LayoutParams params=ll_head.getLayoutParams();
        params.height=((Util.getScreenHeight(this)-getStatusBarHeight())/35)*2;
        ll_head.setLayoutParams(params);
    }
    /**
     * 获取状态栏高度
     * @return
     */
    public  int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result =  getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
