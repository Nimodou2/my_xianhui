package com.maibo.lvyongsheng.xianhui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.ProjectAddListViewExpandAdapter;
import com.maibo.lvyongsheng.xianhui.entity.Card;
import com.maibo.lvyongsheng.xianhui.entity.Consume;
import com.maibo.lvyongsheng.xianhui.entity.CustemProjects;
import com.maibo.lvyongsheng.xianhui.entity.Project;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/13.
 */
public class AddProjectActivity extends BaseActivity {
    List<Project> pro1;
    ExpandableListView lv_item_plan;
    SharedPreferences sp;
    SharedPreferences sp1;
    String apiURL;
    String token;
    int[] sel;
    //产品中被选项
    int[] sel_product;
    //顾客ID
    int cusId;
    List<CustemProjects> list1;
    private ProjectAddListViewExpandAdapter adapter;
    ProgressDialog dialog;
    @Bind(R.id.tv_head)
    TextView tv_head;

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    list1 = (List<CustemProjects>) msg.obj;
                    List<Project> dataPr = list1.get(0).getList();
                    //处理项目数据 排列顺序：1、带卡项且选中的 2、已计划的 3、带卡项的 4、其它
                    pro1=new ArrayList<>();
                    List<Project> pro2=new ArrayList<>();
                    List<Project> pro3=new ArrayList<>();
                    List<Project> pro4=new ArrayList<>();
                    int[] sel1=list1.get(0).getSelected();
                    for (int i=0;i<dataPr.size();i++){
                    //是否有卡项
                    int ka=dataPr.get(i).getCard_list().size();
                    int lanjie=0;
                    //是否被选中
                    for (int j=0;j<sel1.length;j++){
                        //被选中
                        if(sel1[j]==dataPr.get(i).getItem_id()){
                           //带卡项
                            if (ka>0){
                                pro1.add(dataPr.get(i));
                            }else{
                                //只是被选中
                                pro2.add(dataPr.get(i));
                            }
                            lanjie=2;
                        }
                    }
                        if(ka>0&&lanjie!=2){
                            if(ka>0){
                                //只是带卡项的
                                pro3.add(dataPr.get(i));
                            }
                        }else if (lanjie!=2){
                            pro4.add(dataPr.get(i));
                        }
                }
                    //排序后的数据
                    pro1.addAll(pro2);
                    pro1.addAll(pro3);
                    pro1.addAll(pro4);
                    //获取已选中的计划项目
                    sel = list1.get(0).getSelected();
                    initView();
                    dialog.dismiss();
                    break;
                case 2:
                    sel_product= (int[]) msg.obj;
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        setSingleViewHeightAndWidth(tv_head,viewHeight*15/255,0);
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
        sp1 = getSharedPreferences("checkBox", MODE_PRIVATE);
        lv_item_plan = (ExpandableListView) findViewById(R.id.lv_item_plan);
        adapter = new ProjectAddListViewExpandAdapter(AddProjectActivity.this, pro1, sel,lv_item_plan,viewHeight);
        lv_item_plan.setAdapter(adapter);
        lv_item_plan.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    public void initData() {
        Intent intent = getIntent();
        cusId = intent.getIntExtra("customer_id", 0);
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getplanaddlist")
                .addParams("token", token)
                .addParams("customer_id", cusId + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {}

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        //获取顾客使用项目的数据
                        getProjectData(object);
                    }
                });
    }

    public void getProjectData(JsonObject object) {
        //该顾客所消费的项目信息集合（注：包括所有的项目信息）
        List<CustemProjects> dataCustemProject = new ArrayList<CustemProjects>();
        //所有项目的集合
        List<Project> dataProject = new ArrayList<Project>();
        //先解析Project中的数据
        JsonObject data = object.get("data").getAsJsonObject();
        JsonObject project = data.get("project").getAsJsonObject();
        //解析项目集合
        JsonArray array = project.get("list").getAsJsonArray();
        int total = project.get("total").getAsInt();
        for (JsonElement jsonElement : array) {
            //cardList
            List<Card> cards = new ArrayList<>();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String fullname = jsonObject.get("fullname").getAsString();
            int item_id = jsonObject.get("item_id").getAsInt();
            int item_type = jsonObject.get("item_type").getAsInt();
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
            dataProject.add(new Project(cards, item_id, fullname, item_type));
        }
        //解析project中的consume中的数据
        JsonArray consume = project.get("consume").getAsJsonArray();
        List<Consume> con = new ArrayList<>();
        for (JsonElement js3 : consume) {
            JsonObject json3 = js3.getAsJsonObject();
            int item_id = json3.get("item_id").getAsInt();
            String fullname = json3.get("fullname").getAsString();
            String saledate = json3.get("saledate").getAsString();
            con.add(new Consume(item_id, fullname, saledate));
        }
        JsonArray selected = project.get("selected").getAsJsonArray();
        int[] sel = new int[selected.size()];
        int i = -1;
        for (JsonElement js4 : selected) {
            i++;
            int json4 = js4.getAsInt();
            sel[i] = json4;
        }
        dataCustemProject.add(new CustemProjects(dataProject, con, sel, total));
        //传递数据
        Message msg1 = Message.obtain();
        msg1.what = 1;
        msg1.obj = dataCustemProject;
        handler.sendMessage(msg1);
        //解析产品中被计划的数据
        JsonObject product=data.get("product").getAsJsonObject();
        JsonArray selected_product = product.get("selected").getAsJsonArray();
        int[] sel_product=new int[selected_product.size()];
        int pp=-1;
        for (JsonElement js5:selected_product){
            pp++;
            int json5=js5.getAsInt();
            sel_product[pp]=json5;
        }
        Message msg2=Message.obtain();
        msg2.what=2;
        msg2.obj=sel_product;
        handler.sendMessage(msg2);


    }

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
        //将产品中被选项传递到AddTabActivity
        StringBuffer buffer_product=new StringBuffer();
        for (int i=0;i<sel_product.length;i++){
            buffer_product.append(","+sel_product[i]);
        }
        String buffer_product_no=buffer_product.toString();
        if (sel_product.length>0){
            buffer_product_no=buffer_product_no.substring(1);
        }
        String buffer = sb.toString();
        SharedPreferences.Editor  editor=sp.edit();
        editor.putString("bufferProject",buffer);
        editor.putString("buffer_product_no",buffer_product_no);
        editor.putInt("tag",0);
        editor.commit();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }

}
