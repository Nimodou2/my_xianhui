package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.adapter.CardsDetailAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.ConsumRecordAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.CustomerDetailAdapter;
import com.maibo.lvyongsheng.xianhui.adapter.ProjectMsgAdapter;
import com.maibo.lvyongsheng.xianhui.entity.Card;
import com.maibo.lvyongsheng.xianhui.entity.Cards;
import com.maibo.lvyongsheng.xianhui.entity.Custemer;
import com.maibo.lvyongsheng.xianhui.entity.Product;
import com.maibo.lvyongsheng.xianhui.entity.Project;
import com.maibo.lvyongsheng.xianhui.entity.SaleTab;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.maibo.lvyongsheng.xianhui.implement.MyProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LYS on 2016/10/6.
 */
public class CustomerDetailsActivity extends BaseActivity {
    ListView lv_cards;
    TextView cus_name, back;
    SharedPreferences sp;
    String token, apiURL;
    int customer_id;
    List<Cards> list11;
    List<SaleTab> list22;
    List<Card> list33;
    String cardName;
    String cardAmount;
    Boolean isLoadingMore = false;
    int currentPageNum;
    int totalPage;
    MyProgressDialog myDialog;
    ConsumRecordAdapter myAdapter;
    Cards cards;

    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //卡包
                    list11 = (List<Cards>) msg.obj;
                    final CustomerDetailAdapter adapters;
                    lv_cards.setAdapter(adapters = new CustomerDetailAdapter(CustomerDetailsActivity.this, list11,viewHeight));

                    lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                            myDialog.show();
                            Cards adapter_cards = (Cards) adapters.getItem(i);
                            Intent intent = new Intent(CustomerDetailsActivity.this, CustomerDetailsActivity.class);
                            intent.putExtra("tag", 13);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Cards", adapter_cards);
                            bundle.putString("cardAmount", adapter_cards.getAmount());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    myDialog.dismiss();
                    break;
                case 1:
                    //消费记录
                    List<SaleTab> listss = (List<SaleTab>) msg.obj;
                    currentPageNum = msg.arg1;
                    totalPage = msg.arg2;
                    if (isLoadingMore && listss != null) {
                        list22.addAll(listss);
                        myAdapter.notifyDataSetChanged();
                    } else {
                        list22.clear();
                        list22 = listss;
                        lv_cards.setAdapter(myAdapter = new ConsumRecordAdapter(CustomerDetailsActivity.this, list22,viewHeight));
                    }
                    myDialog.dismiss();
                    break;
                case 2:
                    //各卡项的操作记录
                    list33 = (List<Card>) msg.obj;
                    //设置适配器SimpleAdapter
                    lv_cards.setAdapter(new CardsDetailAdapter(getApplicationContext(), list33,viewHeight));
                    myDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        list22 = new ArrayList<>();
        myDialog = new MyProgressDialog(this);
        lv_cards = (ListView) findViewById(R.id.lv_cards);
        cus_name = (TextView) findViewById(R.id.cus_name);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        token = sp.getString("token", null);
        apiURL = sp.getString("apiURL", null);

        Intent intent = getIntent();
        customer_id = intent.getIntExtra("customer_id", -1);
        String customer_name = intent.getStringExtra("customer_name");
        int tag = intent.getIntExtra("tag", -1);
        //项目
        Bundle bundle = intent.getExtras();
        //详细信息
        Project pro1 = (Project) bundle.get("pro1");
        Product product1 = (Product) bundle.get("product1");
        //报表参数
        Project pro2 = (Project) bundle.get("pro2");
        Product product2 = (Product) bundle.get("product2");
        //销售权限
        Project pro3 = (Project) bundle.get("pro3");
        Product product3 = (Product) bundle.get("product3");
        //销售门店
        String[] org_list = intent.getStringArrayExtra("org_list");
        //可用卡项
        String[] vipcard_type = intent.getStringArrayExtra("vipcard_type");
        //持卡顾客
        List<Custemer> custemers = (List<Custemer>) bundle.get("custemers");
        //卡明细
        cards = (Cards) bundle.get("Cards");
        cardAmount = bundle.getString("cardAmount");

        if (tag == 0) {
            myDialog.show();
            getServicesData(0, -1);
            cus_name.setText(customer_name);
        } else if (tag == 1) {
            myDialog.show();
            getServicesData(1, -1);
            cus_name.setText(customer_name);
        } else if (tag == 2) {
            setProjectMsg(pro1, 1);
            cus_name.setText("详细信息");
        } else if (tag == 3) {
            setProjectMsg(pro2, 2);
            cus_name.setText("报表参数");
        } else if (tag == 4) {
            setProjectMsg(pro3, 3);
            cus_name.setText("销售权限");
        } else if (tag == 5) {
            String[] right = new String[org_list.length];
            for (int i = 0; i < org_list.length; i++) {
                right[i] = "";
            }
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, org_list, right, 2,viewHeight));
            cus_name.setText("销售门店");
        } else if (tag == 6) {
            String[] right = new String[vipcard_type.length];
            for (int i = 0; i < vipcard_type.length; i++) {
                right[i] = "";
            }
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, vipcard_type, right, 2,viewHeight));
            cus_name.setText("可用卡项");
        } else if (tag == 7) {
            String[] cusName = new String[custemers.size()];
            for (int i = 0; i < custemers.size(); i++) {
                cusName[i] = custemers.get(i).getFullname();
            }

            String[] right = new String[cusName.length];
            for (int i = 0; i < right.length; i++) {
                right[i] = "";
            }
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, cusName, right, 2,viewHeight));
            cus_name.setText("持卡顾客");
        } else if (tag == 8) {
            setProductMsg(product1, 1);
            cus_name.setText("详细信息");
        } else if (tag == 9) {
            setProductMsg(product2, 2);
            cus_name.setText("报表参数");
        } else if (tag == 10) {
            setProductMsg(product3, 3);
            cus_name.setText("销售权限");
        } else if (tag == 11) {
            //产品配料
            cus_name.setText("产品配料");
            String[] left = intent.getStringArrayExtra("left");
            String[] right = intent.getStringArrayExtra("right");
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, left, right, 2,viewHeight));
        } else if (tag == 12) {
            cus_name.setText("预约信息");
            getServicesData(3, -1);
        } else if (tag == 13) {
            cus_name.setText(cards.getFullname());
            getServicesData(2, -1);
        }
    }

    /**
     * 《产品》
     * 1、详细信息
     * 2、报表参数
     * 3、销售权限
     *
     * @param pro
     * @param what
     */
    public void setProductMsg(final Product pro, int what) {
        if (what == 1) {
            String[] left = {"货品类型", "包装单位", "消耗单位", "规格"};
            String[] right = {pro.getItem_class(), pro.getSpec(), pro.getUse_unit(), pro.getUse_spec()};
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, left, right, 2,viewHeight));
        } else if (what == 2) {
            String[] left = {"报表系数", "货的分类"};
            String[] right = {pro.getReport_ratio(), pro.getItem_class()};
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, left, right, 2,viewHeight));
        } else if (what == 3) {
            //记录是否有卡扣项
            int p = 0;
            for (int i = 0; i < pro.getFee_type().length; i++) {
                if (pro.getFee_type()[i].equals("卡扣")) {
                    p = 4;
                } else {
                    p = 2;
                }
            }

            if (p == 2) {
                String[] left = {"销售门店", "付费方式"};
                String[] right = {"全部", "现金、会员卡"};
                lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, left, right, 3,viewHeight));
                lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0) {
                            Intent intent = new Intent(CustomerDetailsActivity.this, CustomerDetailsActivity.class);
                            intent.putExtra("org_list", pro.getOrg_list());
                            intent.putExtra("tag", 5);
                            startActivity(intent);
                        }
                    }
                });
            } else if (p == 4) {
                String[] left = {"销售门店", "付费方式", "随卡打折", "可用卡项"};
                String[] right = {"全部", "卡扣", pro.getCard_discount(), "有"};
                lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, left, right, 3,viewHeight));
                lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0) {
                            Intent intent = new Intent(CustomerDetailsActivity.this, CustomerDetailsActivity.class);
                            intent.putExtra("org_list", pro.getOrg_list());
                            intent.putExtra("tag", 5);
                            startActivity(intent);
                        }
                        if (i == 3) {
                            Intent intent = new Intent(CustomerDetailsActivity.this, CustomerDetailsActivity.class);
                            intent.putExtra("vipcard_type", pro.getVipcard_type());
                            intent.putExtra("tag", 6);
                            startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    /**
     * 《项目》
     * 1、项目详细信息
     * 2、报表参数
     * 3、销售权限
     *
     * @param pro
     */
    public void setProjectMsg(final Project pro, int what) {
        if (what == 1) {
            String[] left = {"项目部位", "针对部位", "单价", "操作时长", "操作类型", "产品配料"};
            String[] right = {pro.getProject_type(), pro.getProject_class(), pro.getRetail_price(), pro.getHours() + "h", pro.getOp_type(), ""};
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, left, right, 1,viewHeight));
            lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 5) {
                        String[] left = new String[pro.getFormula().size()];
                        String[] right = new String[pro.getFormula().size()];
                        for (int j = 0; j < pro.getFormula().size(); j++) {
                            left[j] = pro.getFormula().get(j).getFullname();
                            right[j] = pro.getFormula().get(j).getQyt();
                        }
                        Intent intent = new Intent(CustomerDetailsActivity.this, CustomerDetailsActivity.class);
                        intent.putExtra("left", left);
                        intent.putExtra("right", right);
                        intent.putExtra("tag", 11);
                        startActivity(intent);
                    }
                }
            });
        } else if (what == 2) {
            String[] left = {"报表系数", "手工费计算方式", "手工费"};
            String[] right = {pro.getReport_ratio(), pro.getManual_type(), pro.getManual_fee()};
            lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, left, right, 2,viewHeight));
        } else if (what == 3) {
            //记录是否有卡扣项
            int p = 0;
            for (int i = 0; i < pro.getFee_type().length; i++) {
                if (pro.getFee_type()[i].equals("卡扣")) {
                    p = 4;
                } else {
                    p = 2;
                }
            }

            if (p == 2) {
                String[] left = {"销售门店", "付费方式"};
                String[] right = {"", "现金、会员卡"};
                lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, left, right, 3,viewHeight));
                lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0) {
                            Intent intent = new Intent(CustomerDetailsActivity.this, CustomerDetailsActivity.class);
                            intent.putExtra("org_list", pro.getOrg_list());
                            intent.putExtra("tag", 5);
                            startActivity(intent);
                        }
                    }
                });
            } else if (p == 4) {
                String[] left = {"销售门店", "付费方式", "随卡打折", "可用卡项"};
                String[] right = {"全部", "卡扣", pro.getCard_discount(), "有"};
                lv_cards.setAdapter(new ProjectMsgAdapter(CustomerDetailsActivity.this, left, right, 3,viewHeight));
                lv_cards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0) {
                            Intent intent = new Intent(CustomerDetailsActivity.this, CustomerDetailsActivity.class);
                            intent.putExtra("org_list", pro.getOrg_list());
                            intent.putExtra("tag", 5);
                            startActivity(intent);
                        }
                        if (i == 3) {
                            Intent intent = new Intent(CustomerDetailsActivity.this, CustomerDetailsActivity.class);
                            intent.putExtra("vipcard_type", pro.getVipcard_type());
                            intent.putExtra("tag", 6);
                            startActivity(intent);
                        }
                    }
                });
            }
        }

    }

    /**
     * 获取服务器数据
     *
     * @param what
     */
    public void getServicesData(int what, int where) {

        if (what == 0) {
            getCardsData();
        } else if (what == 1) {
            getConsumeRecordData();
        } else if (what == 2) {
            getCardsMsg(where);
        } else if (what == 3) {
            getYuyueMsg();
        }
    }

    /**
     * 获取顾客预约信息
     */
    public void getYuyueMsg() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getcustomerschedulelist")
                .addParams("token", token)
                .addParams("customer_id", customer_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("yuyue:", response);
                    }
                });
    }

    /**
     * 获取卡明细
     *
     * @param where
     */
    public void getCardsMsg(int where) {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getcustomercardlog")
                .addParams("token", token)
                .addParams("card_num", cards.getCard_num())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //解析卡明细
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        JsonObject data = jsonObject.get("data").getAsJsonObject();
                        JsonArray rows = data.get("rows").getAsJsonArray();
                        List<Card> list = new ArrayList<Card>();
                        for (JsonElement jsonElement : rows) {
                            JsonObject jo = jsonElement.getAsJsonObject();
                            String fullname = "";
                            String amount = "";
                            String date = "";
                            int item_type = -1;
                            int item_id = -1;
                            if (!jo.get("fullname").isJsonNull())
                                fullname = jo.get("fullname").getAsString();
                            if (!jo.get("item_id").isJsonNull())
                                item_id = jo.get("item_id").getAsInt();
                            if (!jo.get("amount").isJsonNull())
                                amount = jo.get("amount").getAsString();
                            if (!jo.get("item_type").isJsonNull())
                                item_type = jo.get("item_type").getAsInt();
                            if (!jo.get("date").isJsonNull())
                                date = jo.get("date").getAsString();
                            list.add(new Card(fullname, item_id, amount, item_type, date));
                        }
                        Message msg = Message.obtain();
                        msg.what = 2;
                        msg.obj = list;
                        handler.sendMessage(msg);
                    }
                });
    }

    /**
     * 获取卡包信息
     */
    public void getCardsData() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getcustomercardlist")
                .addParams("token", token)
                .addParams("customer_id", customer_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        if (jsonObject.get("data").isJsonArray()) {
                            JsonArray data = jsonObject.get("data").getAsJsonArray();
                            List<Cards> list1 = new ArrayList<Cards>();
                            for (JsonElement jsonElement : data) {
                                JsonObject jo = jsonElement.getAsJsonObject();
                                int card_sort = -1;
                                String card_class = "";
                                String amount = "";
                                String card_num = "";
                                int item_id = 0;
                                String fullname = "";
                                if (!jo.get("card_sort").isJsonNull())
                                    card_sort = jo.get("card_sort").getAsInt();
                                if (!jo.get("card_class").isJsonNull())
                                    card_class = jo.get("card_class").getAsString();
                                if (!jo.get("amount").isJsonNull())
                                    amount = jo.get("amount").getAsString();
                                if (!jo.get("card_num").isJsonNull())
                                    card_num = jo.get("card_num").getAsString();
                                if (!jo.get("item_id").isJsonNull())
                                    item_id = jo.get("item_id").getAsInt();
                                if (!jo.get("fullname").isJsonNull())
                                    fullname = jo.get("fullname").getAsString();

                                JsonArray project_list = jo.get("project_list").getAsJsonArray();
                                List<Card> list2 = new ArrayList<Card>();
                                for (JsonElement jsonElement1 : project_list) {
                                    JsonObject jsonObject1 = jsonElement1.getAsJsonObject();
                                    int item_id1 = -1;
                                    String fullname1 = "";
                                    String price = "";
                                    int times = 0;
                                    if (!jsonObject1.get("item_id").isJsonNull())
                                        item_id1 = jsonObject1.get("item_id").getAsInt();
                                    if (!jsonObject1.get("fullname").isJsonNull())
                                        fullname1 = jsonObject1.get("fullname").getAsString();
                                    if (!jsonObject1.get("price").isJsonNull())
                                        price = jsonObject1.get("price").getAsString();
                                    if (!jsonObject1.get("times").isJsonNull())
                                        times = jsonObject1.get("times").getAsInt();
                                    list2.add(new Card(fullname1, times, card_class, card_num, price, item_id1));
                                }
                                list1.add(new Cards(card_sort, card_class, amount, card_num, item_id, fullname, list2));
                            }
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.obj = list1;
                            handler.sendMessage(msg);
                        }
                        myDialog.dismiss();
                    }
                });
    }

    /**
     * 获取消费记录
     */
    public void getConsumeRecordData() {
        OkHttpUtils
                .post()
                .url(apiURL + "/rest/employee/getcustomerconsumelist")
                .addParams("token", token)
                .addParams("customer_id", customer_id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // Log.e("获取消费记录",response);
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String msg_status = jsonObject.get("status").getAsString();
                        String message = jsonObject.get("message").getAsString();
                        if (msg_status.equals("ok")) {
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            int pageNumber = -1;
                            int totalPage = -1;
                            if (!data.get("pageNumber").isJsonNull())
                                pageNumber = data.get("pageNumber").getAsInt();
                            if (!data.get("totalPage").isJsonNull())
                                totalPage = data.get("totalPage").getAsInt();
                            JsonArray rows = data.get("rows").getAsJsonArray();
                            List<SaleTab> list2 = new ArrayList<SaleTab>();
                            for (JsonElement jsonElement : rows) {
                                JsonObject jo = jsonElement.getAsJsonObject();
                                String saledate = "";
                                String qty = "";
                                String amount = "";
                                int item_id = -1;
                                String fullname = "";
                                if (!jo.get("saledate").isJsonNull())
                                    saledate = jo.get("saledate").getAsString();
                                if (!jo.get("qty").isJsonNull())
                                    qty = jo.get("qty").getAsString();
                                if (!jo.get("amount").isJsonNull())
                                    amount = jo.get("amount").getAsString();
                                if (!jo.get("item_id").isJsonNull())
                                    item_id = jo.get("item_id").getAsInt();
                                if (!jo.get("fullname").isJsonNull())
                                    fullname = jo.get("fullname").getAsString();
                                list2.add(new SaleTab(saledate, qty, amount, item_id, fullname));
                            }
                            Message msg = Message.obtain();
                            msg.what = 1;
                            msg.arg1 = pageNumber;
                            msg.arg2 = totalPage;
                            msg.obj = list2;
                            handler.sendMessage(msg);
                        } else {
                            App.showToast(getApplication(), message);
                        }
                    }
                });
    }

    //排序
    class MyComparator implements Comparator {
        //这里的o1和o2就是list里任意的两个对象，然后按需求把这个方法填完整就行了
        @Override
        public int compare(Object o1, Object o2) {
            Cards lhs = (Cards) o1;
            Cards rhs = (Cards) o2;
            if (lhs.getItem_id() > rhs.getItem_id()) {
                return 1;
            }
            if (lhs.getItem_id() == rhs.getItem_id()) {
                return 0;
            }
            if (lhs.getItem_id() < rhs.getItem_id()) {
                return -1;
            }
            return 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
