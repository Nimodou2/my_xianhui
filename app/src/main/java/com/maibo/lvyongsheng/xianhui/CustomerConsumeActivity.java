package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.ConsumRecordAdapter;
import com.maibo.lvyongsheng.xianhui.entity.SaleTab;
import com.maibo.lvyongsheng.xianhui.view.RefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by LYS on 2016/10/31.
 */
public class CustomerConsumeActivity extends Activity implements RefreshListView.OnRefreshListener{

    SharedPreferences sp;
    String token,apiURL;
    int customer_id;
    TextView tv_customer_name,back;
    RefreshListView lv_cards;
    Boolean isLoadingMore=false;
    int currentPageNum;
    int totalPage;
    List<SaleTab> list22;
    List<SaleTab> list33;
    ConsumRecordAdapter myAdapter;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            currentPageNum=msg.arg1;
            totalPage=msg.arg2;
            //消费记录
            List<SaleTab> listss=(List<SaleTab>) msg.obj;
            currentPageNum=msg.arg1;
            totalPage=msg.arg2;
            if (isLoadingMore&&listss!=null){
                list22.addAll(0,listss);
                myAdapter.notifyDataSetChanged();
                lv_cards.setSelection(list22.size()-list33.size());
            }else{
                list22.clear();
                list22=listss;
                lv_cards.setAdapter(myAdapter=new ConsumRecordAdapter(CustomerConsumeActivity.this,list22));
            }
            list33.addAll(0,listss);
            lv_cards.completeRefresh();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_consume);
        list22=new ArrayList<>();
        list33=new ArrayList<>();
        sp=getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token=sp.getString("token",null);
        apiURL=sp.getString("apiURL",null);
        back= (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_customer_name= (TextView) findViewById(R.id.tv_customer_name);
        lv_cards= (RefreshListView) findViewById(R.id.lv_cards);
        lv_cards.setOnRefreshListener(this);
        Intent intent=getIntent();
        customer_id=intent.getIntExtra("customer_id",-1);
        String customer_name=intent.getStringExtra("customer_name");
        tv_customer_name.setText(customer_name);
        getConsumeRecordData(1);
    }
    @Override
    public void onPullRefresh() {
        //上滑加载更多
        isLoadingMore=true;
        if (currentPageNum!=totalPage){
            getConsumeRecordData(currentPageNum+1);
        }else{
            App.showToast(this,"已加载全部!");
            lv_cards.completeRefresh();
        }
    }

    @Override
    public void onLoadingMore() {
        lv_cards.completeRefresh();
    }
    /**
     * 获取消费记录
     */
    public void getConsumeRecordData(int pageNum){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/getcustomerconsumelist")
                .addParams("token",token)
                .addParams("customer_id",customer_id+"")
                .addParams("pageNumber",pageNum+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // Log.e("获取消费记录",response);
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        String msg_status=jsonObject.get("status").getAsString();
                        String message=jsonObject.get("message").getAsString();
                        if (msg_status.equals("ok")){
                            JsonObject data=jsonObject.get("data").getAsJsonObject();
                            int pageNumber=-1;
                            int totalPage=-1;
                            if (!data.get("pageNumber").isJsonNull())
                                pageNumber=data.get("pageNumber").getAsInt();
                            if (!data.get("totalPage").isJsonNull())
                                totalPage=data.get("totalPage").getAsInt();
                            JsonArray rows=data.get("rows").getAsJsonArray();
                            List<SaleTab> list2=new ArrayList<SaleTab>();
                            for (JsonElement jsonElement:rows){
                                JsonObject jo=jsonElement.getAsJsonObject();
                                String saledate="";
                                String qty="";
                                String amount="";
                                int item_id=-1;
                                String fullname="";
                                if (!jo.get("saledate").isJsonNull())
                                    saledate=jo.get("saledate").getAsString();
                                if (!jo.get("qty").isJsonNull())
                                    qty=jo.get("qty").getAsString();
                                if (!jo.get("amount").isJsonNull())
                                    amount=jo.get("amount").getAsString();
                                if (!jo.get("item_id").isJsonNull())
                                    item_id=jo.get("item_id").getAsInt();
                                if (!jo.get("fullname").isJsonNull())
                                    fullname=jo.get("fullname").getAsString();
                                list2.add(new SaleTab(saledate,qty,amount,item_id,fullname));
                            }
                            Message msg=Message.obtain();
                            msg.what=1;
                            msg.arg1=pageNumber;
                            msg.arg2=totalPage;
                            msg.obj=list2;
                            handler.sendMessage(msg);
                        }else{
                            App.showToast(getApplication(),message);
                        }
                    }
                });
    }

}
