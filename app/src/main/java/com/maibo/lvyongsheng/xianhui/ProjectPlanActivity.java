package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.XiangMuPlanAdapter;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.Card;
import com.maibo.lvyongsheng.xianhui.entity.Consume;
import com.maibo.lvyongsheng.xianhui.entity.CustemProducts;
import com.maibo.lvyongsheng.xianhui.entity.CustemProjects;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.entity.Product;
import com.maibo.lvyongsheng.xianhui.entity.Project;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.greenrobot.event.EventBus;
import okhttp3.Call;

/**
 * Created by LYS on 2016/9/13.
 */
public class ProjectPlanActivity extends BaseActivity {

    SharedPreferences sp;
    String apiURL;
    String token;
    ListView planItem;
    TextView tv_back;
    ImageView iv_add;
    List<CustemProjects> list1;
    List<CustemProducts> list2;
    //adapter填充数据
    List<String> listArray1;
    List<String> listArray2;
    //    ProgressDialog dialog;
    //用于判断View使第几次创建
//    int m = 0;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Bind(R.id.tv_customer_plan)
    TextView tv_customer_plan;
    @Bind(R.id.ll_add_item)
    LinearLayout ll_add_item;

    @Bind(R.id.in_no_datas)
    LinearLayout in_no_datas;
    @Bind(R.id.in_loading_error)
    LinearLayout in_loading_error;
    @Bind(R.id.ll_all_data)
    LinearLayout ll_all_data;

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    ll_all_data.setVisibility(View.GONE);
                    in_loading_error.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    ll_all_data.setVisibility(View.VISIBLE);
                    in_loading_error.setVisibility(View.GONE);
                    list1 = (List<CustemProjects>) msg.obj;
                    listArray1 = new ArrayList<>();
                    int[] sel = list1.get(0).getSelected();
                    List<Project> pro = list1.get(0).getList();
                    for (Project pp : pro) {
                        for (int i = 0; i < sel.length; i++) {
                            if (sel[i] == pp.getItem_id()) {
                                listArray1.add(pp.getFullname());
                            }
                        }
                    }
                    break;
                case 2:
                    ll_all_data.setVisibility(View.VISIBLE);
                    in_loading_error.setVisibility(View.GONE);
                    list2 = (List<CustemProducts>) msg.obj;
                    int[] sel1 = list2.get(0).getSelected();
                    listArray2 = new ArrayList<>();
                    List<Product> pro1 = list2.get(0).getProduct();
                    for (Product product : pro1) {
                        for (int i = 0; i < sel1.length; i++) {
                            if (sel1[i] == product.getItem_id()) {
                                listArray2.add(product.getFullname());
                            }
                        }
                    }
                    //展示项目计划和产品计划
                    planItem.setAdapter(new XiangMuPlanAdapter(ProjectPlanActivity.this, listArray1, listArray2, viewHeight));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        m = 1;
        setContentView(R.layout.activity_xiangmu_plan);
        adapterLitterBar(ll_head);
        setCurrentHeightAndWidth();
        CloseAllActivity.getScreenManager().pushActivity(this);
        showShortDialog();

        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);
        planItem = (ListView) findViewById(R.id.planItem);
        //该处改变ListView的分割线高度和颜色（必须先设置颜色再改变高度，否则无效）
        planItem.setDivider(new ColorDrawable(getResources().getColor(R.color.weixin_lianxiren_gray)));
        planItem.setDividerHeight(3);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //增加项目点击事件
        ll_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = getIntent();
                //获取顾客ID
                int cusId = intent1.getIntExtra("customer_id", 0);
                String fullName = intent1.getStringExtra("customer_name");
                Intent intent = new Intent(ProjectPlanActivity.this, AddTabActivity.class);
                intent.putExtra("customer_name", fullName);
                intent.putExtra("customer_id", cusId);
                startActivity(intent);
            }
        });
        //初始化数据
        EventBus.getDefault().register(this);
        initData();

    }

    /**
     * 适配当前条目
     */
    private void setCurrentHeightAndWidth() {
        View views[] = new View[2];
        int heights[] = new int[2];
        views[0] = tv_customer_plan;
        views[1] = ll_add_item;
        heights[0] = viewHeight * 15 / 255;
        heights[1] = viewHeight * 20 / 255;
        setViewHeightAndWidth(views, heights, null);

    }


    public void initData() {
        Intent intent = getIntent();
        int cusId = intent.getIntExtra("customer_id", 0);
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getplanaddlist")
                .addParams("token", token)
                .addParams("customer_id", cusId + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        handler.sendMessage(msg);
                        dismissShortDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //Log.e("杨建静:",response);
                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                        //获取顾客使用项目的数据
                        getProjectData(object);
                        //获取顾客使用产品的数据
                        getProductData(object);
                        dismissShortDialog();

                    }
                });
    }

    public void onEvent(EventDatas event) {
        if (event.getTag().equals(Constants.PLAN_PROJECT)) {
            initData();
        }
    }

    /**
     * 解析产品数据
     *
     * @param object
     */
    public void getProductData(JsonObject object) {
        //该顾客所消费的项目信息集合（注：包括所有的项目信息）
        List<CustemProducts> dataCustemProduct = new ArrayList<>();
        //所有项目的集合
        List<Product> dataProduct = new ArrayList<>();
        String status = object.get("status").getAsString();
        String message = object.get("message").getAsString();
        if (status.equals("ok")) {
            //先解析Product中的数据
            JsonObject data = object.get("data").getAsJsonObject();
            JsonObject product = data.get("product").getAsJsonObject();
            //解析项目集合
            JsonArray array = product.get("list").getAsJsonArray();
            int total = product.get("total").getAsInt();

            for (JsonElement jsonElement : array) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                List<Card> cards = new ArrayList<>();
                String fullname = jsonObject.get("fullname").getAsString();
                int item_id = jsonObject.get("item_id").getAsInt();
                int item_type = jsonObject.get("item_type").getAsInt();
                JsonArray array1 = jsonObject.get("card_list").getAsJsonArray();
                if (array1.size() > 0) {
                    for (JsonElement jsonElement1 : array1) {
                        JsonObject jsonObjects = jsonElement1.getAsJsonObject();
                        String fullname1 = jsonObjects.get("fullname").getAsString();
                        int times = jsonObjects.get("times").getAsInt();
                        String card_class = jsonObjects.get("card_class").getAsString();
                        String card_num = jsonObjects.get("card_num").getAsString();
                        String price = jsonObjects.get("price").getAsString();
                        int item_ids = jsonObjects.get("item_id").getAsInt();
                        cards.add(new Card(fullname1, times, card_class, card_num, price, item_ids));
                    }
                }

                dataProduct.add(new Product(item_id, fullname, item_type));
            }
            //解析project中的consume中的数据
            JsonArray consume = product.get("consume").getAsJsonArray();
            List<Consume> con = new ArrayList<>();
            for (JsonElement js3 : consume) {
                JsonObject json3 = js3.getAsJsonObject();
                int item_id = json3.get("item_id").getAsInt();
                String fullname = json3.get("fullname").getAsString();
                String saledate = json3.get("saledate").getAsString();
                con.add(new Consume(item_id, fullname, saledate));
            }
            JsonArray selected = product.get("selected").getAsJsonArray();
            int[] sel = new int[selected.size()];
            int i = -1;
            for (JsonElement js4 : selected) {
                i++;
                int json4 = js4.getAsInt();
                sel[i] = json4;
            }
            dataCustemProduct.add(new CustemProducts(dataProduct, con, sel, total));
            //传递数据(待用)
            Message msg2 = Message.obtain();
            msg2.what = 2;
            msg2.obj = dataCustemProduct;
            handler.sendMessage(msg2);
        } else {
            showToast(message);
        }


    }

    /**
     * 解析项目数据
     *
     * @param object
     */
    public void getProjectData(JsonObject object) {
        //该顾客所消费的项目信息集合（注：包括所有的项目信息）
        List<CustemProjects> dataCustemProject = new ArrayList<CustemProjects>();
        //所有项目的集合
        List<Project> dataProject = new ArrayList<Project>();
        String status = object.get("status").getAsString();
        String message = object.get("message").getAsString();
        if (status.equals("ok")) {
            //先解析Project中的数据
            JsonObject data = object.get("data").getAsJsonObject();
            JsonObject project = data.get("project").getAsJsonObject();
            //解析项目集合
            JsonArray array = project.get("list").getAsJsonArray();
            int total = project.get("total").getAsInt();
            for (JsonElement jsonElement : array) {
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
                        int times = jsonObjects.get("times").getAsInt();
                        String card_class = jsonObjects.get("card_class").getAsString();
                        String card_num = jsonObjects.get("card_num").getAsString();
                        String price = jsonObjects.get("price").getAsString();
                        int item_ids = jsonObjects.get("item_id").getAsInt();
                        cards.add(new Card(fullname1, times, card_class, card_num, price, item_ids));
                    }
                }
                dataProject.add(new Project(cards, item_id, fullname, item_type, ""));
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
        } else {
            showToast(message);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        CloseAllActivity.getScreenManager().popActivity(this);
    }

    /**
     * 网络问题，重新加载
     *
     * @param view
     */
    public void loadingMore(View view) {
        showShortDialog();
        initData();
    }
}
