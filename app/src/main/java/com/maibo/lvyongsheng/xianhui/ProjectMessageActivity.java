package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.entity.Custemer;
import com.maibo.lvyongsheng.xianhui.entity.Material;
import com.maibo.lvyongsheng.xianhui.entity.Order;
import com.maibo.lvyongsheng.xianhui.entity.Project;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.DrawRoundCorner;
import com.maibo.lvyongsheng.xianhui.utils.NetWorkUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/10/8.
 */
public class ProjectMessageActivity extends BaseActivity implements View.OnClickListener {
    TextView tv_head_picture, cus_name, cus_grade, cus_files_num, tv_detal_msg, tv_baobiao, tv_power, tv_people_num, tv_percent, back, tv_order;
    LinearLayout ll_have_cards_cus;
    ImageView cus_head;

    SharedPreferences sp;
    String token, apiURL;

    Project pro, pro1, pro2, pro3;
    List<Custemer> custemers;
    int project_id;
    String project_name;
    int id = -1;
    List<Order> orderList;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.in_no_datas)
    LinearLayout in_no_datas;
    @Bind(R.id.in_loading_error)
    LinearLayout in_loading_error;
    @Bind(R.id.ll_all_data)
    LinearLayout ll_all_data;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    pro = (Project) msg.obj;
                    setUIData();
                    break;
                case 1:
                    pro1 = (Project) msg.obj;
                    break;
                case 2:
                    pro2 = (Project) msg.obj;
                    break;
                case 3:
                    pro3 = (Project) msg.obj;
                    break;
                case 4:
                    custemers = (List<Custemer>) msg.obj;
                    break;
                case 5:
                    orderList = (List<Order>) msg.obj;
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_message);
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        showShortDialog();

        tv_head_picture = (TextView) findViewById(R.id.tv_head_picture);
        cus_name = (TextView) findViewById(R.id.cus_name);
        cus_grade = (TextView) findViewById(R.id.cus_grade);
        cus_files_num = (TextView) findViewById(R.id.cus_files_num);
        tv_detal_msg = (TextView) findViewById(R.id.tv_detal_msg);
        tv_baobiao = (TextView) findViewById(R.id.tv_baobiao);
        tv_power = (TextView) findViewById(R.id.tv_power);
        tv_people_num = (TextView) findViewById(R.id.tv_people_num);
        tv_percent = (TextView) findViewById(R.id.tv_percent);
        ll_have_cards_cus = (LinearLayout) findViewById(R.id.ll_have_cards_cus);
        cus_head = (ImageView) findViewById(R.id.cus_head);
        setSingleViewHeightAndWidth(cus_head, viewHeight * 30 / 255, viewHeight * 30 / 255);
        tv_order = (TextView) findViewById(R.id.tv_order);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        tv_detal_msg.setOnClickListener(this);
        tv_baobiao.setOnClickListener(this);
        tv_power.setOnClickListener(this);
        ll_have_cards_cus.setOnClickListener(this);


        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token = sp.getString("token", null);
        apiURL = sp.getString("apiURL", null);

        Intent intent = getIntent();
        project_id = intent.getIntExtra("project_id", -1);
        project_name = intent.getStringExtra("projectName");

        if (NetWorkUtils.isNetworkConnected(this)){
            setOrderListener();
            getServiceData();
            getProjectOrder(project_id);
        }else{
            ll_all_data.setVisibility(View.GONE);
            in_loading_error.setVisibility(View.VISIBLE);
            showToast(R.string.net_connect_error);
            dismissShortDialog();
        }


    }

    /**
     * 订单按钮监听器
     */
    private void setOrderListener() {
        tv_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到订单界面
                Intent intent = new Intent(ProjectMessageActivity.this, OrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("projectOrder", (Serializable) orderList);
                intent.putExtra("projectName", project_name);
                intent.putExtras(bundle);
                intent.putExtra("tag", 1);
                intent.putExtra("project_id", project_id);
                startActivity(intent);
            }
        });
    }

    //填充界面数据
    public void setUIData() {
        if (TextUtils.isEmpty(pro.getAvator_url())) {
            tv_head_picture.setVisibility(View.VISIBLE);
            cus_head.setVisibility(View.GONE);
            if (pro.getFullname().length() > 1)
                tv_head_picture.setText(pro.getFullname().substring(0, 2));
            else tv_head_picture.setText(pro.getFullname());
            tv_head_picture.setTextSize(16);
        } else {
            tv_head_picture.setVisibility(View.GONE);
            cus_head.setVisibility(View.VISIBLE);
            OkHttpUtils
                    .get()
                    .url(pro.getAvator_url())
                    .build()
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(Bitmap response, int id) {
                            Bitmap bm = DrawRoundCorner.makeRoundCorner(response, 63);
                            Drawable drawable = new BitmapDrawable(bm);
                            cus_head.setImageDrawable(drawable);
                        }
                    });
        }

        cus_name.setText(pro.getFullname());
        cus_grade.setText("品牌:" + pro.getBrand_name());
        cus_files_num.setText("编号:" + pro.getProject_code());
        tv_people_num.setText(pro.getCard_total() + "人");
        tv_percent.setText(pro.getSatisfy_rate());
    }

    //获取项目详细信息
    public void getServiceData() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gethelperprojectdetail")
                .addParams("token", token)
                .addParams("project_id", project_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissShortDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //Log.e("ProjectMessage:",response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String status = jsonObject.get("status").getAsString();
                        String message = jsonObject.get("message").getAsString();
                        if (status.equals("ok")) {
                            analysisJson(jsonObject);
                        } else {
                            App.showToast(getApplicationContext(), message);
                        }
                        dismissShortDialog();
                    }
                });
    }

    /**
     * 解析Json
     * @param jsonObject
     */
    private void analysisJson(JsonObject jsonObject) {
        JsonObject data = jsonObject.get("data").getAsJsonObject();
        //解析本页信息
        String project_code = data.get("project_code").getAsString();
        String fullname = data.get("fullname").getAsString();
        String brand_name = data.get("brand_name").getAsString();
        int card_total = data.get("card_total").getAsInt();
        String satisfy_rate = data.get("satisfy_rate").getAsString();
        String avator_url = data.get("avator_url").getAsString();
        Project pro = new Project(fullname, brand_name, card_total, satisfy_rate, project_code, avator_url);
        Message msg = Message.obtain();
        msg.what = 0;
        msg.obj = pro;
        handler.sendMessage(msg);
        //解析详细信息
        String project_type = data.get("project_type").getAsString();
        String project_class = data.get("project_class").getAsString();
        String retail_price = data.get("retail_price").getAsString();
        String hours = data.get("hours").getAsString();
        String op_type = data.get("op_type").getAsString();
        JsonArray formula = data.get("formula").getAsJsonArray();
        List<Material> materials = new ArrayList<Material>();
        for (JsonElement jsonElement : formula) {
            JsonObject jo = jsonElement.getAsJsonObject();
            int item_id = jo.get("item_id").getAsInt();
            String fullnames = jo.get("fullname").getAsString();
            String qty = jo.get("qty").getAsString();
            String use_spec = jo.get("use_spec").getAsString();
            materials.add(new Material(item_id, fullnames, qty, use_spec));
        }
        Project pro1 = new Project(project_type, project_class, retail_price, hours, op_type, materials);
        Message msg1 = Message.obtain();
        msg1.what = 1;
        msg1.obj = pro1;
        handler.sendMessage(msg1);
        //解析报表参数
        String report_ratio = data.get("report_ratio").getAsString();
        String manual_type = data.get("manual_type").getAsString();
        String manual_fee = data.get("manual_fee").getAsString();
        Project pro2 = new Project(report_ratio, manual_type, manual_fee);
        Message msg2 = Message.obtain();
        msg2.what = 2;
        msg2.obj = pro2;
        handler.sendMessage(msg2);
        //解析销售权限
        JsonArray org_list_array = data.get("org_list").getAsJsonArray();
        String[] org_list = new String[org_list_array.size()];
        for (int i = 0; i < org_list_array.size(); i++) {
            org_list[i] = org_list_array.get(i).getAsString();
        }
        JsonArray fee_type_array = data.get("fee_type").getAsJsonArray();
        String[] fee_type = new String[fee_type_array.size()];
        for (int i = 0; i < fee_type_array.size(); i++) {
            fee_type[i] = fee_type_array.get(i).getAsString();
        }
        String card_discount = data.get("card_discount").getAsString();
        JsonArray vipcard_type_array = data.get("vipcard_type").getAsJsonArray();
        String[] vipcard_type = new String[vipcard_type_array.size()];
        for (int i = 0; i < vipcard_type_array.size(); i++) {
            vipcard_type[i] = vipcard_type_array.get(i).getAsString();
        }
        Project pro3 = new Project(org_list, fee_type, card_discount, vipcard_type);
        Message msg3 = Message.obtain();
        msg3.what = 3;
        msg3.obj = pro3;
        handler.sendMessage(msg3);
        //解析持卡顾客
        JsonArray card_customer_list = data.get("card_customer_list").getAsJsonArray();
        List<Custemer> cus = new ArrayList<Custemer>();
        for (JsonElement jsonElement : card_customer_list) {
            JsonObject jo = jsonElement.getAsJsonObject();
            int customer_id = jo.get("customer_id").getAsInt();
            String cusname = jo.get("fullname").getAsString();
            Custemer custemer = new Custemer();
            custemer.setFullname(cusname);
            custemer.setCustomer_id(customer_id);
            cus.add(custemer);
        }
        Message msg4 = Message.obtain();
        msg4.what = 4;
        msg4.obj = cus;
        handler.sendMessage(msg4);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_detal_msg:
                //跳转到详细信息界面
                Intent intent1 = new Intent(ProjectMessageActivity.this, CustomerDetailsActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("pro1", pro1);
                intent1.putExtra("tag", 2);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case R.id.tv_baobiao:
                //跳转到报表参数界面
                Intent intent2 = new Intent(ProjectMessageActivity.this, CustomerDetailsActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("pro2", pro2);
                intent2.putExtra("tag", 3);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
            case R.id.tv_power:
                //跳转到销售权限界面
                Intent intent3 = new Intent(ProjectMessageActivity.this, CustomerDetailsActivity.class);
                Bundle bundle3 = new Bundle();
                bundle3.putSerializable("pro3", pro3);
                intent3.putExtra("tag", 4);
                intent3.putExtras(bundle3);
                startActivity(intent3);
                break;
            case R.id.ll_have_cards_cus:
                //跳转到持卡顾客界面
                Intent intent4 = new Intent(ProjectMessageActivity.this, CustomerDetailsActivity.class);
                Bundle bundle4 = new Bundle();
                bundle4.putSerializable("custemers", (Serializable) custemers);
                intent4.putExtra("tag", 7);
                intent4.putExtras(bundle4);
                startActivity(intent4);
                break;
        }
    }

    //获取订单信息
    public void getProjectOrder(int project_id) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/gethelperprojectorders")
                .addParams("token", token)
                .addParams("project_id", project_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String statuss = jsonObject.get("status").getAsString();
                        String message = jsonObject.get("message").getAsString();
                        if (statuss.equals("ok")) {
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            JsonArray rows = data.get("rows").getAsJsonArray();
                            List<Order> list = new ArrayList<Order>();
                            for (JsonElement jsonElement : rows) {
                                JsonObject jo = jsonElement.getAsJsonObject();
                                int schedule_id = jo.get("schedule_id").getAsInt();
                                int customer_id = jo.get("customer_id").getAsInt();
                                String status = jo.get("status").getAsString();
                                String start_time = jo.get("start_time").getAsString();
                                String end_time = jo.get("end_time").getAsString();
                                String engineer_id = jo.get("engineer_id").getAsString();
                                String bed_name = jo.get("bed_name").getAsString();
                                String project_code = jo.get("project_code").getAsString();
                                String project_name = jo.get("project_name").getAsString();
                                String customer_name = jo.get("customer_name").getAsString();
                                String engineer_name = jo.get("engineer_name").getAsString();

                                list.add(new Order(schedule_id, customer_id, status, start_time, end_time, engineer_id,
                                        bed_name, project_code, project_name, customer_name, engineer_name));
                            }
                            Message msg = Message.obtain();
                            msg.what = 5;
                            msg.obj = list;
                            handler.sendMessage(msg);

                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }

    /**
     * 网络问题，重新加载
     * @param view
     */
    public void loadingMore(View view){
        showShortDialog();
        if (NetWorkUtils.isNetworkConnected(this)){
            ll_all_data.setVisibility(View.VISIBLE);
            in_loading_error.setVisibility(View.GONE);
            setOrderListener();
            getServiceData();
            getProjectOrder(project_id);
        }else{
            ll_all_data.setVisibility(View.GONE);
            in_loading_error.setVisibility(View.VISIBLE);
            showToast(R.string.net_connect_error);
            dismissShortDialog();
        }
    }
}
