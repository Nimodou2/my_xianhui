package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.entity.Custemer;
import com.maibo.lvyongsheng.xianhui.entity.Order;
import com.maibo.lvyongsheng.xianhui.entity.Product;
import com.maibo.lvyongsheng.xianhui.implement.DrawRoundCorner;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by LYS on 2016/10/8.
 */
public class ProductMessageActivity extends Activity implements View.OnClickListener{
    TextView tv_head_picture,cus_name,cus_grade,cus_files_num,tv_detal_msg,tv_baobiao,tv_power
            ,tv_people_num,tv_percent,back,tv_order;
    LinearLayout ll_have_cards_cus,ll_satisfy;
    ImageView cus_head;

    SharedPreferences sp;
    String token,apiURL;

    Product project,pro,pro1,pro2,pro3;
    List<Custemer> custemers;
    List<Order> orderList;
    ProgressDialog dialog;
    int id=-1;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    pro=(Product) msg.obj;
                    setUIData();
                    break;
                case 1:
                    pro1=(Product) msg.obj;
                    break;
                case 2:
                    pro2=(Product) msg.obj;
                    break;
                case 3:
                    pro3=(Product) msg.obj;
                    break;
                case 4:
                    custemers=(List<Custemer>) msg.obj;
                    break;
                case 5:
                    orderList=(List<Order>) msg.obj;
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_message);
        dialog=new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        dialog.show();
        tv_head_picture=(TextView) findViewById(R.id.tv_head_picture);
        cus_name=(TextView) findViewById(R.id.cus_name);
        cus_grade=(TextView) findViewById(R.id.cus_grade);
        cus_files_num=(TextView) findViewById(R.id.cus_files_num);
        tv_detal_msg=(TextView) findViewById(R.id.tv_detal_msg);
        tv_baobiao=(TextView) findViewById(R.id.tv_baobiao);
        tv_power=(TextView) findViewById(R.id.tv_power);
        tv_people_num=(TextView) findViewById(R.id.tv_people_num);
        tv_percent=(TextView) findViewById(R.id.tv_percent);
        ll_have_cards_cus=(LinearLayout) findViewById(R.id.ll_have_cards_cus);
        ll_satisfy = (LinearLayout) findViewById(R.id.ll_satisfy);
        cus_head= (ImageView) findViewById(R.id.cus_head);
        tv_order= (TextView) findViewById(R.id.tv_order);
        back= (TextView) findViewById(R.id.back);

        Intent intent=getIntent();
        final int item_id=intent.getIntExtra("Item_id",-1);
        final String product_name=intent.getStringExtra("productName");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到订单界面
                Intent intent = new Intent(ProductMessageActivity.this, OrderActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("productOrder",(Serializable) orderList);
                intent.putExtra("item_id",item_id);
                intent.putExtra("productName",product_name);
                intent.putExtras(bundle);
                intent.putExtra("tag",0);
                startActivity(intent);
            }
        });

        ll_satisfy.setVisibility(View.GONE);
        tv_detal_msg.setOnClickListener(this);
        tv_baobiao.setOnClickListener(this);
        tv_power.setOnClickListener(this);
        ll_have_cards_cus.setOnClickListener(this);

        sp=getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token=sp.getString("token",null);
        apiURL=sp.getString("apiURL",null);


        getServiceData(item_id);
        getOrderList(item_id);

    }
    //填充界面数据
    public void setUIData(){
        cus_name.setText(pro.getFullname());
        if (TextUtils.isEmpty(pro.getAvator_url())){
            tv_head_picture.setVisibility(View.VISIBLE);
            cus_head.setVisibility(View.GONE);
            if (pro.getFullname().length()>1)
                tv_head_picture.setText(pro.getFullname().substring(0,2));
            else tv_head_picture.setText(pro.getFullname());
            tv_head_picture.setTextSize(16);
        }else{
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
                            Bitmap bm= DrawRoundCorner.makeRoundCorner(response,63);
                            Drawable drawable =new BitmapDrawable(bm);
                            cus_head.setImageDrawable(drawable);
                            dialog.dismiss();
                        }
                    });
        }
        cus_grade.setText("品牌:"+pro.getBrand_name());
        cus_files_num.setText("编号:"+pro.getItem_code());
        tv_people_num.setText(pro.getCard_total()+"人");
    }
    //获取项目详细信息
    public void getServiceData(int item_id){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/gethelperproductdetail")
                .addParams("token",token)
                .addParams("item_id",item_id+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("product:",response);
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        JsonObject data=jsonObject.get("data").getAsJsonObject();
                        //解析本页信息
                        String fullname="";
                        String brand_name="";
                        String item_code="";
                        String card_total="";
                        String avator_url="";
                        if (!data.get("fullname").isJsonNull()){
                            fullname=data.get("fullname").getAsString();
                        }
                        if (!data.get("brand_name").isJsonNull()){
                            brand_name=data.get("brand_name").getAsString();
                        }
                        if (!data.get("item_code").isJsonNull()) {
                            item_code=data.get("item_code").getAsString();
                        }
                        if (!data.get("card_total").isJsonNull()){
                            card_total=data.get("card_total").getAsString();
                        }
                        if (!data.get("avator_url").isJsonNull()){
                            avator_url=data.get("avator_url").getAsString();
                        }
                        Product pro=new Product(fullname,brand_name,item_code, card_total,avator_url);
                        Message msg=Message.obtain();
                        msg.what=0;
                        msg.obj=pro;
                        handler.sendMessage(msg);

                        //解析详细信息
                        String item_class="";
                        String spec="";
                        String use_unit="";
                        String use_spec="";
                        if (!data.get("item_class").isJsonNull()){
                            item_class=data.get("item_class").getAsString();
                        }
                        if (!data.get("spec").isJsonNull()){
                            spec=data.get("spec").getAsString();
                        }
                        if(!data.get("use_unit").isJsonNull()){
                            use_unit=data.get("use_unit").getAsString();
                        }
                        if (!data.get("use_spec").isJsonNull()){
                            use_spec=data.get("use_spec").getAsString();
                        }
                        Product pro1=new Product();
                        pro1.setItem_class(item_class);
                        pro1.setSpec(spec);
                        pro1.setUse_unit(use_unit);
                        pro1.setUse_spec(use_spec);
                        Message msg1=Message.obtain();
                        msg1.what=1;
                        msg1.obj=pro1;
                        handler.sendMessage(msg1);
                        //解析报表参数
                        String report_ratio="";
                        if (!data.get("report_ratio").isJsonNull()){
                            report_ratio=data.get("report_ratio").getAsString();
                        }
                        Product pro2=new Product();
                        pro2.setReport_ratio(report_ratio);
                        pro2.setItem_class(item_class);
                        Message msg2=Message.obtain();
                        msg2.what=2;
                        msg2.obj=pro2;
                        handler.sendMessage(msg2);
                        //解析销售权限

                        JsonArray org_list_array=data.get("org_list").getAsJsonArray();
                        String[] org_list=new String[org_list_array.size()];
                        for(int i=0;i<org_list_array.size();i++){
                            org_list[i]=org_list_array.get(i).getAsString();
                        }
                        JsonArray fee_type_array=data.get("fee_type").getAsJsonArray();
                        String[] fee_type=new String[fee_type_array.size()];
                        for (int i=0;i<fee_type_array.size();i++){
                            fee_type[i]=fee_type_array.get(i).getAsString();
                        }
                        String card_discount=data.get("card_discount").getAsString();
                        JsonArray vipcard_type_array=data.get("vipcard_type").getAsJsonArray();
                        String[] vipcard_type=new String[vipcard_type_array.size()];
                        for (int i=0;i<vipcard_type_array.size();i++){
                            vipcard_type[i]=vipcard_type_array.get(i).getAsString();
                        }
                        Product pro3=new Product(org_list,fee_type,card_discount,vipcard_type);
                        Message msg3=Message.obtain();
                        msg3.what=3;
                        msg3.obj=pro3;
                        handler.sendMessage(msg3);
                        //解析持卡顾客
                        JsonArray card_customer_list=data.get("card_customer_list").getAsJsonArray();
                        List<Custemer> cus=new ArrayList<Custemer>();
                        int customer_id=0;
                        String cusname="";
                        for (JsonElement jsonElement:card_customer_list){
                            JsonObject jo=jsonElement.getAsJsonObject();
                            if (!jo.get("customer_id").isJsonNull()){
                                customer_id=jo.get("customer_id").getAsInt();
                            }
                            if (!jo.get("fullname").isJsonNull()){
                                cusname=jo.get("fullname").getAsString();
                            }
                            Custemer custemer=new Custemer();
                            custemer.setFullname(cusname);
                            custemer.setCustomer_id(customer_id);
                            cus.add(custemer);
                        }
                        Message msg4=Message.obtain();
                        msg4.what=4;
                        msg4.obj=cus;
                        handler.sendMessage(msg4);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_detal_msg:
                Intent intent1=new Intent(ProductMessageActivity.this,CustomerDetailsActivity.class);
                Bundle bundle1=new Bundle();
                bundle1.putSerializable("product1",pro1);
                intent1.putExtra("tag",8);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case R.id.tv_baobiao:
                Intent intent2=new Intent(ProductMessageActivity.this,CustomerDetailsActivity.class);
                Bundle bundle2=new Bundle();
                bundle2.putSerializable("product2",pro2);
                intent2.putExtra("tag",9);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
            case R.id.tv_power:
                Intent intent3=new Intent(ProductMessageActivity.this,CustomerDetailsActivity.class);
                Bundle bundle3=new Bundle();
                bundle3.putSerializable("product3",pro3);
                intent3.putExtra("tag",10);
                intent3.putExtras(bundle3);
                startActivity(intent3);
                break;
            case R.id.ll_have_cards_cus:
                Intent intent4=new Intent(ProductMessageActivity.this,CustomerDetailsActivity.class);
                Bundle bundle4=new Bundle();
                bundle4.putSerializable("custemers",(Serializable) custemers);
                intent4.putExtra("tag",7);
                intent4.putExtras(bundle4);
                startActivity(intent4);
                break;
        }
    }

    /**
     * 获取单个产品的订单明细
     */
    public void getOrderList(int item_id){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/gethelperproductorders")
                .addParams("token",token)
                .addParams("item_id",""+item_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        JsonObject data=jsonObject.get("data").getAsJsonObject();
                        JsonArray rows=data.get("rows").getAsJsonArray();
                        List<Order> list=new ArrayList<Order>();
                        for (JsonElement jsonElement:rows){
                            JsonObject jo=jsonElement.getAsJsonObject();
                            String flowno=jo.get("flowno").getAsString();
                            int cstid=jo.get("cstid").getAsInt();
                            String engineer_id=jo.get("engineer_id").getAsString();
                            int item_id=jo.get("item_id").getAsInt();
                            String item_name=jo.get("item_name").getAsString();
                            String qty=jo.get("qty").getAsString();
                            String customer_name=jo.get("customer_name").getAsString();
                            //String engineer_name=jo.get("engineer_name").getAsString();
                            String status=jo.get("status").getAsString();
                            list.add(new Order(flowno,cstid,engineer_id,item_id,item_name,qty,customer_name,status));
                        }
                        Message msg=Message.obtain();
                        msg.what=5;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                });
    }
}
