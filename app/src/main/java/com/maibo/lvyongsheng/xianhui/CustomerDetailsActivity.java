package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.ConsumRecordAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.CustomerDetailAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.ProjectMsgAdapter;
import com.maibo.lvyongsheng.xianhui.entity.Card;
import com.maibo.lvyongsheng.xianhui.entity.Cards;
import com.maibo.lvyongsheng.xianhui.entity.Custemer;
import com.maibo.lvyongsheng.xianhui.entity.Product;
import com.maibo.lvyongsheng.xianhui.entity.Project;
import com.maibo.lvyongsheng.xianhui.entity.SaleTab;
import com.maibo.lvyongsheng.xianhui.implement.MyProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by LYS on 2016/10/6.
 */
public class CustomerDetailsActivity extends Activity{
    ListView lv_cards;
    TextView cus_name,back;
    SharedPreferences sp;
    String token,apiURL;
    int customer_id;
    List<Cards> list11;
    List<SaleTab> list22;
    List<Card> list33;
    List<Cards> ls1;
    String cardName;
    String cardAmount;
    Boolean isLoadingMore=false;
    int currentPageNum;
    int totalPage;
    MyProgressDialog myDialog;
    ConsumRecordAdapter myAdapter;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //卡包
                   list11=(List<Cards>) msg.obj;
                    lv_cards.setAdapter(new CustomerDetailAdapter(CustomerDetailsActivity.this,list11));
                    //对list11先排序再删除重复部分
                    ls1=new ArrayList<>();
                    List<Cards> ls2=new ArrayList<>();
                    for (int i=0;i<list11.size();i++){
                        if(list11.get(i).getCard_sort()==1){
                            ls1.add(list11.get(i));
                        }else {
                            ls2.add(list11.get(i));
                        }

                    }
                    Collections.sort(ls2, new MyComparator());
                    ls1.addAll(ls2);
                    for (int i = 0; i < ls1.size()-1; i++) {
                        for (int j = ls1.size()-1; j > i; j--) {
                            if (ls1.get(j).getItem_id()==(ls1.get(i).getItem_id())) {
                                ls1.remove(j);
                            }
                        }
                    }

                    lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            myDialog.show();
                            getServicesData(2,i);
                            cardName=ls1.get(i).getFullname();
                            cardAmount=ls1.get(i).getAmount();
                        }
                    });
                    myDialog.dismiss();
                    break;
                case 1:
                    //消费记录
                    List<SaleTab> listss=(List<SaleTab>) msg.obj;
                    currentPageNum=msg.arg1;
                    totalPage=msg.arg2;
                    if (isLoadingMore&&listss!=null){
                        list22.addAll(listss);
                        myAdapter.notifyDataSetChanged();
                    }else{
                        list22.clear();
                        list22=listss;
                        lv_cards.setAdapter(myAdapter=new ConsumRecordAdapter(CustomerDetailsActivity.this,list22));
                    }
                    myDialog.dismiss();
                    break;
                case 2:
                    //各卡项的操作记录
                    list33=(List<Card>) msg.obj;
                    //设置适配器SimpleAdapter
                    String[] left=new String[list33.size()+1];
                    String[] right=new String[list33.size()+1];
                    for(int i=0;i<list33.size()+1;i++){
                        if(i==0){
                            left[0]="储值";
                            right[0]=cardAmount;
                        }else{
                            left[i]=list33.get(i-1).getFullname();
                            right[i]=list33.get(i-1).getAmount();
                        }
                    }
                    List<Map<String,String>> list=new ArrayList<>();
                    for(int i=0;i<list33.size()+1;i++){
                        Map<String,String> map=new HashMap<>();
                        map.put("left1",left[i]);
                        map.put("right1",right[i]);
                        list.add(map);
                    }
                    SimpleAdapter simpleAdapter=new SimpleAdapter(CustomerDetailsActivity.this,list,R.layout.style_list_detail,new String[]{"left1","right1"}
                            ,new int[]{R.id.name,R.id.numbers});
                    //再次跳转到本界面

                    lv_cards.setAdapter(simpleAdapter);
                    myDialog.dismiss();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        list22=new ArrayList<>();
        myDialog=new MyProgressDialog(this);
        lv_cards=(ListView)findViewById(R.id.lv_cards);
        cus_name = (TextView) findViewById(R.id.cus_name);
        back= (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sp=getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token=sp.getString("token",null);
        apiURL=sp.getString("apiURL",null);

        Intent intent=getIntent();
        customer_id=intent.getIntExtra("customer_id",-1);
        String customer_name=intent.getStringExtra("customer_name");
        int tag=intent.getIntExtra("tag",-1);
        //项目
        Bundle bundle=intent.getExtras();
        //详细信息
        Project pro1=(Project) bundle.get("pro1");
        Product product1=(Product)bundle.get("product1");
        //报表参数
        Project pro2=(Project) bundle.get("pro2");
        Product product2=(Product)bundle.get("product2");
        //销售权限
        Project pro3=(Project) bundle.get("pro3");
        Product product3=(Product)bundle.get("product3");
        //销售门店
        String[] org_list=intent.getStringArrayExtra("org_list");
        //可用卡项
        String[] vipcard_type=intent.getStringArrayExtra("vipcard_type");
        //持卡顾客
        List<Custemer> custemers=(List<Custemer>) bundle.get("custemers");


        if (tag==0) {
            myDialog.show();
            getServicesData(0,-1);
            cus_name.setText(customer_name);
        } else if (tag==1){
            myDialog.show();
            getServicesData(1,-1);
            cus_name.setText(customer_name);
        }else if(tag==2){
            setProjectMsg(pro1,1);
            cus_name.setText("详细信息");
        }else if(tag==3){
            setProjectMsg(pro2,2);
            cus_name.setText("报表参数");
        }else if(tag==4){
            setProjectMsg(pro3,3);
            cus_name.setText("销售权限");
        }else if(tag==5){
            String[] right=new String[org_list.length];
            for (int i=0;i<org_list.length;i++){
                right[i]="";
            }
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,org_list,right,2));
            cus_name.setText("销售门店");
        }else if(tag==6){
            String[] right=new String[vipcard_type.length];
            for (int i=0;i<vipcard_type.length;i++){
                right[i]="";
            }
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,vipcard_type,right,2));
            cus_name.setText("可用卡项");
        }else if (tag==7){
            String[] cusName=new String[custemers.size()];
            for (int i=0;i<custemers.size();i++){
                cusName[i]=custemers.get(i).getFullname();
            }

            String[] right=new String[cusName.length];
            for (int i=0;i<right.length;i++){
                right[i]="";
            }
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,cusName,right,2));
            cus_name.setText("持卡顾客");
        }else if(tag==8){
            setProductMsg(product1,1);
            cus_name.setText("详细信息");
        }else if(tag==9){
            setProductMsg(product2,2);
            cus_name.setText("报表参数");
        }else if(tag==10){
            setProductMsg(product3,3);
            cus_name.setText("销售权限");
        }else if(tag==11){
            //产品配料
            cus_name.setText("产品配料");
            String[] left=intent.getStringArrayExtra("left");
            String[] right=intent.getStringArrayExtra("right");
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,left,right,2));
        }else if(tag==12){
            cus_name.setText("预约信息");
            getServicesData(3,-1);
        }
    }

    /**《产品》
     * 1、详细信息
     * 2、报表参数
     * 3、销售权限
     * @param pro
     * @param what
     */
    public void setProductMsg(final Product pro,int what){
        if (what==1){
            String[] left={"货品类型","包装单位","消耗单位","规格"};
            String[] right={pro.getItem_class(),pro.getSpec(),pro.getUse_unit(),pro.getUse_spec()};
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,left,right,2));
        }else if(what==2){
            String[] left={"报表系数","货的分类"};
            String[] right={pro.getReport_ratio(),pro.getItem_class()};
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,left,right,2));
        }else if(what==3){
            //记录是否有卡扣项
            int p=0;
            for(int i=0;i<pro.getFee_type().length;i++){
                if(pro.getFee_type()[i].equals("卡扣")){
                    p=4;
                }else{
                    p=2;
                }
            }

            if(p==2){
                String[] left={"销售门店","付费方式"};
                String[] right={"全部","现金、会员卡"};
                lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,left,right,3));
                lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i==0){
                            Intent intent=new Intent(CustomerDetailsActivity.this,CustomerDetailsActivity.class);
                            intent.putExtra("org_list",pro.getOrg_list());
                            intent.putExtra("tag",5);
                            startActivity(intent);
                        }
                    }
                });
            }else if(p==4){
                String[] left={"销售门店","付费方式","随卡打折","可用卡项"};
                String[] right={"全部","卡扣",pro.getCard_discount(),"有"};
                lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,left,right,3));
                lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i==0){
                            Intent intent=new Intent(CustomerDetailsActivity.this,CustomerDetailsActivity.class);
                            intent.putExtra("org_list",pro.getOrg_list());
                            intent.putExtra("tag",5);
                            startActivity(intent);
                        }
                        if(i==3){
                            Intent intent=new Intent(CustomerDetailsActivity.this,CustomerDetailsActivity.class);
                            intent.putExtra("vipcard_type",pro.getVipcard_type());
                            intent.putExtra("tag",6);
                            startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    /**《项目》
     * 1、项目详细信息
     * 2、报表参数
     * 3、销售权限
     * @param pro
     */
    public void setProjectMsg(final Project pro,int what){
        if(what==1){
            String[] left={"项目部位","针对部位","单价","操作时长","操作类型","产品配料"};
            String[] right={pro.getProject_type(),pro.getProject_class(),pro.getRetail_price(),pro.getHours()+"h",pro.getOp_type(),""};
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,left,right,1));
            lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i==5){
                        String[] left=new String[pro.getFormula().size()];
                        String[] right=new String[pro.getFormula().size()];
                        for (int j=0;j<pro.getFormula().size();j++){
                            left[j]=pro.getFormula().get(j).getFullname();
                            right[j]=pro.getFormula().get(j).getQyt();
                        }
                        Intent intent=new Intent(CustomerDetailsActivity.this,CustomerDetailsActivity.class);
                        intent.putExtra("left",left);
                        intent.putExtra("right",right);
                        intent.putExtra("tag",11);
                        startActivity(intent);
                    }
                }
            });
        }else if(what==2){
            String[] left={"报表系数","手工费计算方式","手工费"};
            String[] right={pro.getReport_ratio(),pro.getManual_type(),pro.getManual_fee()};
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,left,right,2));
        }else if(what==3){
            //记录是否有卡扣项
            int p=0;
           for(int i=0;i<pro.getFee_type().length;i++){
               if(pro.getFee_type()[i].equals("卡扣")){
                p=4;
               }else{
                   p=2;
               }
           }

            if(p==2){
                String[] left={"销售门店","付费方式"};
                String[] right={"","现金、会员卡"};
                lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,left,right,3));
                lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i==0){
                            Intent intent=new Intent(CustomerDetailsActivity.this,CustomerDetailsActivity.class);
                            intent.putExtra("org_list",pro.getOrg_list());
                            intent.putExtra("tag",5);
                            startActivity(intent);
                        }
                    }
                });
            }else if(p==4){
                String[] left={"销售门店","付费方式","随卡打折","可用卡项"};
                String[] right={"全部","卡扣",pro.getCard_discount(),"有"};
                lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this,left,right,3));
                lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i==0){
                            Intent intent=new Intent(CustomerDetailsActivity.this,CustomerDetailsActivity.class);
                            intent.putExtra("org_list",pro.getOrg_list());
                            intent.putExtra("tag",5);
                            startActivity(intent);
                        }
                        if(i==3){
                            Intent intent=new Intent(CustomerDetailsActivity.this,CustomerDetailsActivity.class);
                            intent.putExtra("vipcard_type",pro.getVipcard_type());
                            intent.putExtra("tag",6);
                            startActivity(intent);
                        }
                    }
                });
            }
        }

    }
    /**
     * 获取服务器数据
     * @param what
     */
    public void getServicesData(int what,int where){

        if(what==0){
            getCardsData();
        }else if (what==1){
            getConsumeRecordData();
        }else if(what==2){
            getCardsMsg(where);
        }else if(what==3){
            getYuyueMsg();
        }
    }

    /**
     * 获取顾客预约信息
     */
    public void getYuyueMsg(){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/getcustomerschedulelist")
                .addParams("token",token)
                .addParams("customer_id",customer_id+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("yuyue:",response);
                    }
                });
    }
    /**
     * 获取卡明细
     * @param where
     */
    public void getCardsMsg(int where){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/getcustomercardlog")
                .addParams("token",token)
                .addParams("card_num",ls1.get(where).getCard_num())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                       //解析卡明细
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        JsonObject data=jsonObject.get("data").getAsJsonObject();
                        JsonArray rows=data.get("rows").getAsJsonArray();
                        List<Card> list=new ArrayList<Card>();
                        for (JsonElement jsonElement:rows){
                            JsonObject jo=jsonElement.getAsJsonObject();
                            String fullname=jo.get("fullname").getAsString();
                            String amount=jo.get("amount").getAsString();
                            //int times=jo.get("times").getAsInt();
                            list.add(new Card(fullname,0,amount));
                        }
                        Message msg=Message.obtain();
                        msg.what=2;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                });
    }

    /**
     * 获取卡包信息
     */
    public void getCardsData(){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/getcustomercardlist")
                .addParams("token",token)
                .addParams("customer_id",customer_id+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject=new JsonParser().parse(response).getAsJsonObject();
                        JsonArray data=jsonObject.get("data").getAsJsonArray();
                        List<Cards> list1=new ArrayList<Cards>();
                        for (JsonElement jsonElement:data){
                            JsonObject jo=jsonElement.getAsJsonObject();
                            int card_sort=jo.get("card_sort").getAsInt();
                            String card_class=jo.get("card_class").getAsString();
                            String amount=jo.get("amount").getAsString();
                            String card_num=jo.get("card_num").getAsString();
                            int item_id=jo.get("item_id").getAsInt();
                            String fullname = jo.get("fullname").getAsString();

                            JsonArray project_list = jo.get("project_list").getAsJsonArray();
                            List<Card> list2=new ArrayList<Card>();
                            for (JsonElement jsonElement1:project_list){
                                JsonObject jsonObject1=jsonElement1.getAsJsonObject();
                                int item_id1=jsonObject1.get("item_id").getAsInt();
                                String fullname1=jsonObject1.get("fullname").getAsString();
                                String price=jsonObject1.get("price").getAsString();
                                int times=jsonObject1.get("times").getAsInt();
                                list2.add(new Card(fullname1,times,card_class,card_num,price,item_id1));
                            }
                            list1.add(new Cards(card_sort,card_class,amount,card_num,item_id,fullname,list2));
                        }
                        Message msg=Message.obtain();
                        msg.what=0;
                        msg.obj=list1;
                        handler.sendMessage(msg);
                    }
                });
    }

    /**
     * 获取消费记录
     */
    public void getConsumeRecordData(){
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/getcustomerconsumelist")
                .addParams("token",token)
                .addParams("customer_id",customer_id+"")
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

    //排序
    class MyComparator implements Comparator
    {
        //这里的o1和o2就是list里任意的两个对象，然后按需求把这个方法填完整就行了
        @Override
        public int compare(Object o1, Object o2) {
            Cards lhs=(Cards) o1;
            Cards rhs=(Cards) o2;
            if (lhs.getItem_id() > rhs.getItem_id())
            {
                return 1;
            }
            if (lhs.getItem_id() == rhs.getItem_id())
            {
                return 0;
            }
            if (lhs.getItem_id() < rhs.getItem_id())
            {
                return -1;
            }
            return 0;
        }
    }
}
