package com.maibo.lvyongsheng.xianhui;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.ProductAddListViewExpandAdapter;
import com.maibo.lvyongsheng.xianhui.entity.Card;
import com.maibo.lvyongsheng.xianhui.entity.Consume;
import com.maibo.lvyongsheng.xianhui.entity.CustemProducts;
import com.maibo.lvyongsheng.xianhui.entity.Product;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/14.
 */

public class AddProductActivity extends BaseActivity{

    SharedPreferences sp,sp2;
    String apiURL;
    String token;
    List<CustemProducts> list1;
    ExpandableListView lv_add_product;
    int[] sel;
    List<Product> pro1;
    int cusId;
    private ProductAddListViewExpandAdapter adapter;
    ProgressDialog dialog;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    list1=(List<CustemProducts>)msg.obj;
                    List<String> listArray= new ArrayList<>();
                    List<Product> pro=list1.get(0).getProduct();
                    //处理项目数据 排列顺序：1、带卡项且选中的 2、已计划的 3、带卡项的 4、其它
                    pro1=new ArrayList<>();
                    List<Product> pro2=new ArrayList<>();
                    List<Product> pro3=new ArrayList<>();
                    List<Product> pro4=new ArrayList<>();
                    int[] sel1=list1.get(0).getSelected();
                    for (int i=0;i<pro.size();i++){
                        //是否有卡项
                        int ka=pro.get(i).getCard_list().size();
                        int lanjie=0;
                        //是否被选中
                        for (int j=0;j<sel1.length;j++){
                            //被选中
                            if(sel1[j]==pro.get(i).getItem_id()){
                                //带卡项
                                if (ka>0){
                                    pro1.add(pro.get(i));
                                }else{
                                    //只是被选中
                                    pro2.add(pro.get(i));
                                }
                            lanjie=2;
                            }
                        }
                        if(ka>0&&lanjie!=2){
                            if(ka>0){
                                //只是带卡项的
                                pro3.add(pro.get(i));
                            }
                        }else if (lanjie!=2){
                            pro4.add(pro.get(i));
                        }

                    }
                    //排序后的数据
                    pro1.addAll(pro2);
                    pro1.addAll(pro3);
                    pro1.addAll(pro4);
                    sel = list1.get(0).getSelected();
                    initView();
                    dialog.dismiss();
                    break;
            }
        }};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setSingleViewHeightAndWidth(ll_head,viewHeight*15/255,0);
        CloseAllActivity.getScreenManager().pushActivity(this);
        dialog=new ProgressDialog(this);
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        dialog.show();
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        initData();

    }
    private void initView() {
        sp2 = getSharedPreferences("checkBoxProduct", MODE_PRIVATE);
        lv_add_product = (ExpandableListView) findViewById(R.id.lv_add_product);
        adapter = new ProductAddListViewExpandAdapter(AddProductActivity.this, pro1, sel,lv_add_product,viewHeight);
        lv_add_product.setAdapter(adapter);
        lv_add_product.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    public void initData(){
        Intent intent=getIntent();
        cusId=intent.getIntExtra("customer_id",0);
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/getplanaddlist")
                .addParams("token",token)
                .addParams("customer_id",cusId+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        //获取顾客使用项目的数据
                        getProductData(object);

                    }
                });
    }

    /**
     * 获取所有项目数据
     * @param object
     */
    public void getProductData(JsonObject object){
        //该顾客所消费的项目信息集合（注：包括所有的项目信息）
        List<CustemProducts> dataCustemProject=new ArrayList<>();
        //所有项目的集合
        List<Product> dataProduct=new ArrayList<>();
        //先解析Product中的数据
        JsonObject data=object.get("data").getAsJsonObject();
        JsonObject product=data.get("product").getAsJsonObject();
        //解析项目集合
        JsonArray array = product.get("list").getAsJsonArray();
        int total = product.get("total").getAsInt();

        for(JsonElement jsonElement:array){
            JsonObject jsonObject=jsonElement.getAsJsonObject();
            List<Card> cards = new ArrayList<>();
            String fullname=jsonObject.get("fullname").getAsString();
            int item_id=jsonObject.get("item_id").getAsInt();
            int item_type=jsonObject.get("item_type").getAsInt();

            JsonArray array1 = jsonObject.get("card_list").getAsJsonArray();
            if (array1.size() > 0) {
                for (JsonElement jsonElement1 : array1) {
                    JsonObject jsonObjects = jsonElement1.getAsJsonObject();
                    String fullname1 = jsonObjects.get("fullname").getAsString();
                     int times=jsonObjects.get("times").getAsInt();
                    String card_class = jsonObjects.get("card_class").getAsString();
                    String card_num = jsonObjects.get("card_num").getAsString();
                    String price = jsonObjects.get("price").getAsString();
                    int item_ids = jsonObjects.get("item_id").getAsInt();
                    cards.add(new Card(fullname1, times, card_class, card_num, price, item_ids));
                }
            }

            dataProduct.add(new Product(item_id,fullname,item_type,cards));
        }
        //解析project中的consume中的数据
        JsonArray consume = product.get("consume").getAsJsonArray();
        List<Consume> con=new ArrayList<>();
        for (JsonElement js3:consume){
            JsonObject json3=js3.getAsJsonObject();
            int item_id=json3.get("item_id").getAsInt();
            String fullname=json3.get("fullname").getAsString();
            String saledate=json3.get("saledate").getAsString();
            con.add(new Consume(item_id,fullname,saledate));
        }
        JsonArray selected = product.get("selected").getAsJsonArray();
        int[] sel=new int[selected.size()];
        int i=-1;
        for (JsonElement js4:selected){
            i++;
            int json4=js4.getAsInt();
            sel[i]=json4;
        }

        dataCustemProject.add(new CustemProducts(dataProduct,con,sel,total));
        //传递数据
        Message msg = Message.obtain();
        msg.what = 1;
        msg.obj = dataCustemProject;
        handler.sendMessage(msg);

    }

    /**
     * 提交项目计划
     */
    @Override
    protected void onPause() {
        super.onPause();
        //提交项目计划
        HashMap<Integer, Integer> itemID = adapter.getItemID();
        int j = 0;
        int k = -1;
        //排除保留的空值
        for (Integer i = 0; i < pro1.size(); i++) {
            if (itemID.get(i) != null) {
                j++;
            }
        }
        int[] itemId = new int[j];
        //保存选中项目对象的item_id
        for (Integer i = 0; i < pro1.size(); i++) {
            if (itemID.get(i) != null) {
                k++;
                itemId[k] = itemID.get(i);
            }
        }
        //将itemId拼接成字符串
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < itemId.length; i++) {
            if (i < itemId.length - 1) {
                sb.append(itemId[i] + ",");
            } else {
                sb.append(itemId[i]);
            }
        }
        String buffer = sb.toString();
        SharedPreferences.Editor  editor=sp.edit();
        editor.putString("bufferProduct",buffer);
        editor.putInt("tag",1);
        editor.commit();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
