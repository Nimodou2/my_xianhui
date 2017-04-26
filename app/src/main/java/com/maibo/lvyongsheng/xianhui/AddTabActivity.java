package com.maibo.lvyongsheng.xianhui;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.MyProgressDialog;
import com.maibo.lvyongsheng.xianhui.implement.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import de.greenrobot.event.EventBus;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/14.
 */
public class AddTabActivity extends TabActivity implements View.OnClickListener {
    private static final String TAG="AddTabActivity";
    private int change_position;
    private int product_choiced;
    private int project_choiced;
    private int totalProject;
    private int totalProduct;
    LinearLayout ll_head;
    SharedPreferences sp;
    String apiURL;
    String token;
    int cusId;
    //    int isQuit = 0;
    ProgressDialog dialog;
    String customer_name;
    TextView tv_back, tv_project, tv_product;
    TabHost tab;
    MyProgressDialog shortDialog;
    LinearLayout in_no_datas, in_loading_error;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String response = (String) msg.obj;
                    JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                    JsonObject data = jo.get("data").getAsJsonObject();
                    JsonObject project = data.get("project").getAsJsonObject();
                    JsonObject product = data.get("product").getAsJsonObject();
                    totalProject = 0;
                    totalProduct = 0;
                    if (!project.get("total").isJsonNull()) {
                        totalProject = project.get("total").getAsInt();
                    }
                    if (!product.get("total").isJsonNull()) {
                        totalProduct = product.get("total").getAsInt();
                    }
                    if (project.get("selected").isJsonArray()) {
                        JsonArray jaProject = project.get("selected").getAsJsonArray();
                        for (JsonElement je : jaProject) {
                            bufferProject += "," + je.getAsInt();
                        }
                    }
                    if (product.get("selected").isJsonArray()) {
                        JsonArray jaProduct = product.get("selected").getAsJsonArray();
                        for (JsonElement je : jaProduct) {
                            bufferProduct += "," + je.getAsInt();
                        }
                    }

                    Intent intent = getIntent();
                    int cusId = intent.getIntExtra("customer_id", 0);
                    Intent intent1 = new Intent(AddTabActivity.this, AddProjectActivity.class);
                    intent1.putExtra("customer_id", cusId);
                    intent1.putExtra("customer_name", customer_name);
                    intent1.putExtra("response", response);
                    tab.addTab(tab.newTabSpec("first").setIndicator("ONE").setContent(intent1));
                    Intent intent2 = new Intent(AddTabActivity.this, AddProductActivity.class);
                    intent2.putExtra("customer_id", cusId);
                    intent2.putExtra("response", response);
                    tab.addTab(tab.newTabSpec("second").setIndicator("TWO").setContent(intent2));
                    tv_project.setText("已选项目(" + totalProject + ")");
                    tv_product.setText("已选产品(" + totalProduct + ")");
                    in_loading_error.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tab);
        CloseAllActivity.getScreenManager().pushActivity(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);

        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        Intent intent3 = getIntent();
        cusId = intent3.getIntExtra("customer_id", 0);
        customer_name = intent3.getStringExtra("customer_name");
        change_position=intent3.getIntExtra("position",-1);
        ll_head = (LinearLayout) findViewById(R.id.ll_head);
        //adapterLitterBar(ll_head);

        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_project = (TextView) findViewById(R.id.tv_project);
        tv_product = (TextView) findViewById(R.id.tv_product);
        in_no_datas = (LinearLayout) findViewById(R.id.in_no_datas);
        in_loading_error = (LinearLayout) findViewById(R.id.in_loading_error);
        tv_back.setOnClickListener(this);
        tv_project.setOnClickListener(this);
        tv_product.setOnClickListener(this);

        tab = getTabHost();
        //请求服务器数据
        shortDialog = new MyProgressDialog(this);
        shortDialog.show();
        getProjectDatas();
        EventBus.getDefault().register(this);

    }

    /**
     * 获取计划添加项目/产品列表
     */
    private void getProjectDatas() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getplanaddlist")
                .addParams("token", token)
                .addParams("customer_id", cusId + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        App.showToast(getApplicationContext(), "网络异常");
                        shortDialog.dismiss();
                        in_loading_error.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("AddProject",response);
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        //获取顾客使用项目的数据
                        String status = object.get("status").getAsString();
                        String message = object.get("message").getAsString();
                        if (status.equals("ok")) {
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.obj = response;
                            handler.sendMessage(msg);
                        } else {
                            App.showToast(getApplicationContext(), message);
                        }
                        shortDialog.dismiss();
                    }
                });
    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (isQuit == 1) {
//            String buffer1 = sp.getString("bufferProduct", null);
//            String buffer2 = sp.getString("bufferProject", null);
//            String buffer_product_no = sp.getString("buffer_product_no", null);
//            int tag = sp.getInt("tag", -1);
//            String buffer = buffer1 + "," + buffer2;
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString("bufferAll", buffer);
//            editor.putString("bufferProduct", null);
//            editor.putString("bufferProject", null);
//            editor.commit();
//
//            //保存计划项目和产品
//            if (tag == 0) {
//                if (buffer_product_no != null && buffer2 != null) {
//                    dialog.show();
//                    String buffer_one = buffer_product_no + "," + buffer2;
//                    savePlanItem(buffer_one);
//                } else if (buffer_product_no != null && buffer2 == null) {
//                    dialog.show();
//                    savePlanItem(buffer_product_no);
//                } else if (buffer_product_no == null && buffer2 != null) {
//                    dialog.show();
//                    savePlanItem(buffer2);
//                } else {
//                    App.showToast(getApplicationContext(), "保存失败");
//                }
//            } else if (tag == 1) {
//                if (buffer1 != null && buffer2 != null) {
//                    String buffer_one = buffer1 + "," + buffer2;
//                    savePlanItem(buffer_one);
//                } else if (buffer1 != null && buffer2 == null) {
//                    savePlanItem(buffer1);
//                } else if (buffer1 == null && buffer2 != null) {
//                    savePlanItem(buffer2);
//                } else {
//                    App.showToast(getApplicationContext(), "保存失败");
//                }
//            }
//        }
//    }

    /**
     * 保存计划到后台
     *
     * @param buffer
     */
    public void savePlanItem(String buffer) {
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
                        App.showToast(getApplicationContext(), "网络连接异常");
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //这个是保存后的返回值
                        Log.e(TAG,"这个是保存后的数据回调   " +response);
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        String status = object.get("status").getAsString();
                        dialog.dismiss();
                        //这里做回掉操作
                        if(productStatus==0){
                            product_choiced=totalProduct;
                        }
                        if(projectStatus==0){
                            project_choiced=totalProject;
                        }
                        EventDatas eventDatas = new EventDatas(Constants.GET_CHANGE_TOTAL,change_position,project_choiced,product_choiced,status);
                        EventBus.getDefault().post(eventDatas);
                        finish();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
//                isQuit = 1;
                if (projectStatus == 0 && productStatus == 0) {
                    finish();
                } else {
                    //处理待提交的数据
                    dialog.show();
                    dealBuffer();

                }
                break;
            case R.id.tv_project:
                tab.setCurrentTabByTag("first");
                tv_project.setBackgroundResource(R.drawable.shap_left_blue_bg);
                tv_project.setTextColor(Color.parseColor("#FF2F8FCA"));
                tv_product.setBackgroundResource(R.drawable.shap_right_blue_nochoose_bg);
                tv_product.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_product:
                tab.setCurrentTabByTag("second");
                tv_project.setBackgroundResource(R.drawable.shap_left_blue_nochoose_bg);
                tv_project.setTextColor(getResources().getColor(R.color.white));
                tv_product.setBackgroundResource(R.drawable.shap_right_blue_bg);
                tv_product.setTextColor(Color.parseColor("#FF2F8FCA"));
                break;
        }
    }

    /**
     * 处理待提交的数据
     */
    private void dealBuffer() {
        String bufferAll = "";
        if (bufferProject.length() > 0) {
            bufferAll = bufferProject.substring(1) + bufferProduct;
        } else {
            if (bufferProduct.length() > 0) {
                bufferAll = bufferProduct.substring(1);
            }
        }
        savePlanItem(bufferAll);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 适配tab的高度
     *
     * @param ll_head
     */
    public void adapterLitterBar(LinearLayout ll_head) {
        ViewGroup.LayoutParams params = ll_head.getLayoutParams();
        params.height = ((Util.getScreenHeight(this) - getStatusBarHeight()) / 34) * 2;
        ll_head.setLayoutParams(params);
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

    private int projectStatus = 0;
    private int productStatus = 0;
    private int haveProject = 0;
    private int havaProduct = 0;
    private String bufferProject = "";
    private String bufferProduct = "";

    public void onEvent(EventDatas event) {
        if (event.getTag().equals(Constants.PLAN_PROJECT_ADAPTER)) {
            if (event.getResponse().equals("0") && productStatus == 0) {
                tv_back.setText("返回");
                projectStatus = 0;
            } else {
                tv_back.setText("保存");
                projectStatus = 1;
            }
            haveProject = 1;
            bufferProject = event.getBuffer();
            tv_project.setText("已选项目(" + event.getMessageStatus() + ")");
            String thisproject=event.getMessageStatus();
            if(thisproject!=null&&thisproject.length()>0){
                project_choiced=Integer.parseInt(event.getMessageStatus());
                Log.e(TAG,"这个是点击后回传的项目选择"+project_choiced);
            }
        } else if (event.getTag().equals(Constants.PLAN_PRODUCT_ADAPTER)) {
            if (event.getResponse().equals("0") && projectStatus == 0) {
                tv_back.setText("返回");
                productStatus = 0;
            } else {
                tv_back.setText("保存");
                productStatus = 1;
            }
            havaProduct = 1;
            tv_product.setText("已选产品(" + event.getMessageStatus() + ")");
            String thisproduct=event.getMessageStatus();
            if(thisproduct!=null&&thisproduct.length()>0){
                product_choiced=Integer.parseInt(thisproduct);
                Log.e(TAG,"这个是点击后回传的产品选择"+product_choiced);
            }
            bufferProduct = event.getBuffer();
        }
    }

    public void loadingMore(View view) {
        shortDialog.show();
        getProjectDatas();
    }
}
